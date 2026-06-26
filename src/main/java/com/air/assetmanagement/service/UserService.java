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

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean hasHeader = isHeaderRow(sheet.getRow(0));

            for (Row row : sheet) {
                if (hasHeader && row.getRowNum() == 0) continue;

                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.getCellType() == CellType.BLANK) continue;

                long id            = (long) idCell.getNumericCellValue();
                String firstName   = row.getCell(1).getStringCellValue().trim();
                String lastName    = row.getCell(2).getStringCellValue().trim();

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
}
