package com.air.assetmanagement.service;

import com.air.assetmanagement.model.User;
import com.air.assetmanagement.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Map<String, Integer> importFromExcel(MultipartFile file) throws IOException {
        List<User> parsed = new ArrayList<>();

        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean hasHeader = isHeaderRow(sheet.getRow(0));

            for (Row row : sheet) {
                if (hasHeader && row.getRowNum() == 0) continue;

                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.getCellType() == CellType.BLANK) continue;

                Long id = parseId(idCell, formatter);
                if (id == null) continue;

                String firstName   = formatter.formatCellValue(row.getCell(1)).trim();
                String lastName    = formatter.formatCellValue(row.getCell(2)).trim();

                parsed.add(new User(id, firstName, lastName));
            }
        }

        Set<Long> existingIds = userRepository
                .findAllById(parsed.stream().map(User::getId).collect(Collectors.toList()))
                .stream().map(User::getId).collect(Collectors.toSet());

        List<User> newUsers = parsed.stream()
                .filter(u -> !existingIds.contains(u.getId()))
                .collect(Collectors.toList());

        userRepository.saveAll(newUsers);

        return Map.of("imported", newUsers.size(), "skipped", parsed.size() - newUsers.size());
    }

    private boolean isHeaderRow(Row row) {
        if (row == null) return false;
        Cell firstCell = row.getCell(0);
        return firstCell != null && firstCell.getCellType() == CellType.STRING;
    }

    /**
     * Reads the id cell whether it is stored as a number or as text.
     * Returns null when the value cannot be parsed as a whole number so the
     * row can be skipped instead of aborting the whole import.
     */
    private Long parseId(Cell idCell, DataFormatter formatter) {
        if (idCell.getCellType() == CellType.NUMERIC) {
            return (long) idCell.getNumericCellValue();
        }
        String raw = formatter.formatCellValue(idCell).trim();
        if (raw.isEmpty()) return null;
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
