package com.enterprise.backend.service;

import com.enterprise.backend.exception.EnterpriseBackendException;
import com.enterprise.backend.model.entity.Category;
import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.request.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private static final String ERROR = "Không thành công";
    private static final String SUCCESS = "Thành công";
    private final ProductService productService;
    private final CategoryService categoryService;

    private void validateFileImage(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST, "File is empty");
        }

        // Get the content type of the file
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST, "File type is unknown");
        }

        // Check if the file is an image
        if (contentType.startsWith("image/")) {
            // Optional: Validate image file extension
            String originalFileName = file.getOriginalFilename();
            if (originalFileName != null && !originalFileName.matches(".*\\.(jpg|jpeg|png|gif|bmp|heic|svg)$")) {
                throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST,
                        "Unsupported image format. Only JPG, JPEG, PNG, GIF, BMP, HEIC, and SVG are allowed.");
            }
        }
        // Check if the file is a PDF
        else if (contentType.equals("application/pdf")) {
            // Optional: Validate PDF file extension
            String originalFileName = file.getOriginalFilename();
            if (originalFileName != null && !originalFileName.matches(".*\\.pdf$")) {
                throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST, "Unsupported file format. Only PDF is allowed.");
            }
        }
        else {
            // If the file is neither an image nor a PDF
            throw new EnterpriseBackendException(ErrorCode.BAD_REQUEST, "Only image and PDF files are allowed");
        }
    }

    public String uploadFiles(HttpServletRequest request,
                              MultipartFile file) {
        validateFileImage(file);

        try {
            File imageDir = new File("./image");

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                log.info("File originalName not found!");
                return null;
            }

            if (!imageDir.exists()) {
                log.info("Create imageDir: {} - {}", file.getOriginalFilename(), imageDir.mkdirs());
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // Format the current date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
            String formattedDate = LocalDateTime.now().format(formatter);

            // Construct the new file name
            String fileName = formattedDate + extension;
            Path path = Paths.get(imageDir.getAbsolutePath() + File.separator + "file_" + fileName);
            int i = 0;
            while (path.toFile().exists()) {
                path = Paths.get(imageDir.getAbsolutePath() + File.separator + "file_" + i + "_" + fileName);
                i++;
            }
            Files.write(path, file.getBytes());

            String domain = request.getScheme() + "://" +
                    (request.getServerName().contains("localhost") ? (request.getServerName() + ":" + request.getLocalPort()) : request.getServerName())
                    + "/api/file";

            return domain + "/images/" + path.getFileName();
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
        }
        return null;
    }

    public ResponseEntity<Resource> getFile(String fileName) {
        try {
            Path filePath = Paths.get("image").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(Files.probeContentType(filePath) != null ?
                                Files.probeContentType(filePath) : "application/octet-stream"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            log.error("Error getting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            log.error("Error determining file type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<byte[]> importProduct(MultipartFile file) {
        try {
            // Sử dụng ByteArrayOutputStream để lưu file kết quả đã xử lý
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Gọi phương thức processExcelFile để xử lý file và ghi vào outputStream
            processExcelFile(file, outputStream);

            // Tạo byte array từ OutputStream
            byte[] byteArray = outputStream.toByteArray();

            // Trả về file đã xử lý dưới dạng file Excel
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"processed-product-file.xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArray);
        } catch (IOException e) {
            // Nếu có lỗi, trả về file txt chứa thông tin lỗi
            String errorMessage = "Error processing file: " + e.getMessage();
            byte[] errorBytes = errorMessage.getBytes();  // Chuyển thông báo lỗi thành byte array

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"error-log.txt\"")
                    .contentType(MediaType.TEXT_PLAIN)  // Đặt Content-Type là text/plain cho file txt
                    .body(errorBytes);
        }
    }

    public void processExcelFile(MultipartFile file, OutputStream outputStream) throws IOException {
        // Lấy InputStream từ MultipartFile
        var inputStream = file.getInputStream();

        // Đọc file Excel từ InputStream
        var workbook = new XSSFWorkbook(inputStream);
        var sheet = workbook.getSheetAt(0);  // Giả sử dữ liệu ở trang tính đầu tiên

        // Lấy dòng tiêu đề (cột)
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> columnMap = new HashMap<>();

        // Duyệt qua các cột trong hàng tiêu đề và lưu tên cột cùng chỉ số vào Map
        for (Cell cell : headerRow) {
            columnMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
        }

        // Thêm cột "Kết quả" và "Lỗi" vào cuối hàng tiêu đề
        int lastCellNum = addResultAndErrorColumnsAndReturnLastCellNum(headerRow);

        // Duyệt qua các hàng còn lại và đọc dữ liệu
        var rowIterator = sheet.iterator();

        // Bỏ qua 3 hàng đầu tiên (hàng 0, 1, 2), bắt đầu từ hàng thứ 4 (hàng có chỉ số 3)
        int rowIndex = 0;  // Dùng để đếm số thứ tự các hàng
        while (rowIterator.hasNext()) {
            var row = rowIterator.next();
            if (rowIndex++ < 3) {
                continue; // Bỏ qua 3 hàng đầu tiên
            }

            Cell resultCell = row.createCell(lastCellNum);
            Cell errorCell = row.createCell(lastCellNum + 1);
            var productRequest = buildProductRequest(row, resultCell, errorCell, workbook, columnMap);
            executeImportDataProduct(productRequest, resultCell, errorCell, workbook);
        }

        workbook.write(outputStream);
        workbook.close();
        inputStream.close();
    }

    public int addResultAndErrorColumnsAndReturnLastCellNum(Row headerRow) {
        // Kiểm tra nếu đã có cột "Kết quả" và "Lỗi"
        boolean hasResultColumn = false;
        boolean hasErrorColumn = false;

        // Lặp qua tất cả các ô trong hàng tiêu đề để kiểm tra sự tồn tại của cột "Kết quả" và "Lỗi"
        int lastCellNum = headerRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String cellValue = cell.getStringCellValue().trim();
                if ("Kết quả".equals(cellValue)) {
                    hasResultColumn = true;
                } else if ("Lỗi".equals(cellValue)) {
                    hasErrorColumn = true;
                }
            }
        }

        // Nếu không có cột "Kết quả", tạo mới
        if (!hasResultColumn) {
            Cell resultHeaderCell = headerRow.createCell(lastCellNum);
            resultHeaderCell.setCellValue("Kết quả");
            lastCellNum++;  // Cập nhật lại chỉ số cột tiếp theo
        }

        // Nếu không có cột "Lỗi", tạo mới
        if (!hasErrorColumn) {
            Cell errorHeaderCell = headerRow.createCell(lastCellNum);
            errorHeaderCell.setCellValue("Lỗi");
            lastCellNum--;  // Cập nhật lại chỉ số cột
        }

        return lastCellNum;
    }

    private void executeImportDataProduct(ProductRequest productRequest,
                                          Cell resultCell,
                                          Cell errorCell,
                                          Workbook workbook) {
        try {
            if (productRequest == null) {
                return;
            }

            StringBuilder error = new StringBuilder();
            if (StringUtils.isEmpty(productRequest.getTitle())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được tên sản phẩm|");
            }

            if (StringUtils.isEmpty(productRequest.getDescription())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được mô tả sản phẩm|");
            }

            if (CollectionUtils.isEmpty(productRequest.getCategoryIds())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được danh mục sản phẩm|");
            }

            if (productRequest.getPrice() == null) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được giá sản phẩm|");
            }

            if (productRequest.getQuantity() == null) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được số lượng trong kho|");
            }

            if (CollectionUtils.isEmpty(productRequest.getImagesUrl())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được ảnh sản phẩm|");
            }

            if (productRequest.getIsFeatured() == null) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được sản phẩm nổi bật|");
            }

            if (StringUtils.isEmpty(productRequest.getGeneralDescription())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được thành phần nguyên liệu|");
            }

            if (StringUtils.isEmpty(productRequest.getInstruction())) {
                setErrorCell(resultCell, workbook);
                error.append("|Không đọc được hướng dẫn sử dụng|");
            }

            if (error.length() > 0) {
                errorCell.setCellValue(error.toString());
                return;
            }

            productService.createProduct(productRequest);
            setSuccessCell(resultCell, workbook);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            // Thêm kết quả và lỗi vào hàng hiện tại
            setErrorCell(resultCell, workbook);
            errorCell.setCellValue(e.getMessage());
        }
    }

    private void setErrorCell(Cell errorCell, Workbook workbook) {
        CellStyle errorStyle = workbook.createCellStyle();
        Font errorFont = workbook.createFont();
        errorFont.setColor(IndexedColors.RED.getIndex());  // Màu đỏ
        errorFont.setBold(true);  // Bạn có thể làm đậm nếu cần
        errorStyle.setFont(errorFont);

        errorCell.setCellValue(ERROR);
        errorCell.setCellStyle(errorStyle);
    }

    private void setSuccessCell(Cell resultCell, Workbook workbook) {
        CellStyle resultStyle = workbook.createCellStyle();
        Font resultFont = workbook.createFont();
        resultFont.setColor(IndexedColors.GREEN.getIndex());  // Màu xanh lá cây
        resultFont.setBold(true);  // Bạn có thể làm đậm nếu cần
        resultStyle.setFont(resultFont);

        resultCell.setCellValue(SUCCESS);
        resultCell.setCellStyle(resultStyle);
    }

    private ProductRequest buildProductRequest(Row row,
                                               Cell resultCell,
                                               Cell errorCell,
                                               Workbook workbook,
                                               Map<String, Integer> columnMap) {
        try {
            // Lấy giá trị của từng cột theo tên cột
            var tenSanPham = getCellValue(row, columnMap, "Tên sản phẩm");
            var moTa = getCellValue(row, columnMap, "Mô tả chung");
            var danhMuc = getCellValue(row, columnMap, "Danh mục");
            var giaSanPhamStr = getCellValue(row, columnMap, "Giá sản phẩm");
            var soLuongKhoStr = getCellValue(row, columnMap, "Số lượng trong kho");
            var anhSanPham = getCellValue(row, columnMap, "Ảnh sản phẩm");
            var sanPhamNoiBatStr = getCellValue(row, columnMap, "Sản phẩm nổi bật");
            var thanhPhanNguyenLieu = getCellValue(row, columnMap, "Thành phần nguyên liệu");
            var huongDanSuDung = getCellValue(row, columnMap, "Hướng dẫn sử dụng");
            boolean sanPhamNoiBat = "TRUE".equalsIgnoreCase(sanPhamNoiBatStr);

            ProductRequest productRequest = new ProductRequest();
            productRequest.setTitle(tenSanPham);
            productRequest.setDescription(moTa);
            productRequest.setGeneralDescription(thanhPhanNguyenLieu);
            productRequest.setInstruction(huongDanSuDung);
            productRequest.setPrice(Long.parseLong(giaSanPhamStr));
            productRequest.setQuantity(Long.parseLong(soLuongKhoStr));
            productRequest.setImagesUrl(List.of(anhSanPham));
            productRequest.setCategoryIds(categoryService.findByName(danhMuc)
                    .stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
            productRequest.setIsFeatured(sanPhamNoiBat);
            productRequest.setType("SAN_PHAM");
            return productRequest;
        } catch (EnterpriseBackendException e) {
            setErrorCell(resultCell, workbook);
            errorCell.setCellValue(e.getDescription());
            return null;
        } catch (Exception e) {
            log.error("Error reading row {}: {}", row.getRowNum(), e.getMessage(), e);
            // Thêm kết quả và lỗi vào hàng hiện tại
            setErrorCell(resultCell, workbook);
            errorCell.setCellValue(e.getMessage());
            return null;
        }
    }

    public String getCellValue(Row row, Map<String, Integer> columnMap, String columnName) {
        Integer columnIndex = columnMap.get(columnName);
        if (columnIndex == null) {
            return ""; // Nếu không tìm thấy tên cột, trả về giá trị rỗng
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        // Kiểm tra kiểu dữ liệu của ô
        switch (cell.getCellType()) {
            case STRING:
                // Nếu ô là kiểu chuỗi, trả về giá trị chuỗi
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Nếu ô là kiểu số, kiểm tra xem có phải là ngày tháng hay không
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Nếu ô chứa ngày tháng, định dạng thành chuỗi
                    return cell.getDateCellValue().toString(); // hoặc sử dụng SimpleDateFormat để định dạng lại nếu cần
                } else {
                    // Nếu ô là kiểu số, chuyển nó thành Long
                    double numericValue = cell.getNumericCellValue();

                    // Kiểm tra nếu là số nguyên
                    if (numericValue == (long) numericValue) {
                        // Nếu là số nguyên, chuyển trực tiếp thành Long
                        return String.valueOf((long) numericValue);
                    } else {
                        // Nếu có phần thập phân, làm tròn hoặc chuyển sang Long
                        return String.valueOf(Math.round(numericValue));
                    }
                }
            case BOOLEAN:
                // Nếu ô là kiểu boolean, trả về giá trị chuỗi của boolean
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Nếu ô chứa công thức, trả về giá trị tính toán của công thức dưới dạng chuỗi
                return String.valueOf(cell.getNumericCellValue());
            case BLANK:
            default:
                // Nếu ô trống hoặc kiểu dữ liệu không xác định, trả về chuỗi rỗng
                return "";
        }
    }
}
