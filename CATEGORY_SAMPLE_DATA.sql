-- ================================================
-- SAMPLE DATA FOR CATEGORY TABLE
-- ================================================
-- Thêm dữ liệu mẫu cho bảng category

USE `marketmini`;

-- Insert root categories (no parent)
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Thực phẩm tươi sống', 'Các loại thực phẩm tươi sống như thịt, cá, rau củ', NULL, 'FRESH', 1, 1),
('Đồ uống', 'Các loại nước uống, nước ngọt, bia rượu', NULL, 'DRINK', 1, 2),
('Bánh kẹo', 'Bánh ngọt, kẹo, snack các loại', NULL, 'SWEET', 1, 3),
('Gia vị', 'Gia vị nấu ăn, đậu phộng, muối', NULL, 'SPICE', 1, 4),
('Đồ gia dụng', 'Các vật dụng gia đình thiết yếu', NULL, 'HOME', 1, 5),
('Sức khỏe & làm đẹp', 'Sản phẩm chăm sóc sức khỏe và làm đẹp', NULL, 'HEALTH', 1, 6);

-- Get parent category IDs for sub-categories
SET @fresh_food_id = (SELECT category_id FROM category WHERE category_code = 'FRESH');
SET @drink_id = (SELECT category_id FROM category WHERE category_code = 'DRINK');
SET @sweet_id = (SELECT category_id FROM category WHERE category_code = 'SWEET');
SET @spice_id = (SELECT category_id FROM category WHERE category_code = 'SPICE');
SET @home_id = (SELECT category_id FROM category WHERE category_code = 'HOME');
SET @health_id = (SELECT category_id FROM category WHERE category_code = 'HEALTH');

-- Insert sub-categories for Thực phẩm tươi sống
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Thịt', 'Thịt heo, bò, gà, vịt', @fresh_food_id, 'MEAT', 1, 1),
('Hải sản', 'Cá, tôm, cua, ghẹ tươi sống', @fresh_food_id, 'SEAFOOD', 1, 2),
('Rau củ quả', 'Rau xanh, củ quả tươi', @fresh_food_id, 'VEGETABLE', 1, 3),
('Trái cây', 'Các loại trái cây tươi', @fresh_food_id, 'FRUIT', 1, 4),
('Trứng & sữa', 'Trứng gà, sữa tươi, sản phẩm từ sữa', @fresh_food_id, 'DAIRY', 1, 5);

-- Insert sub-categories for Đồ uống
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Nước ngọt', 'Coca, Pepsi, Sprite, 7Up', @drink_id, 'SODA', 1, 1),
('Nước suối', 'Nước khoáng, nước tinh khiết', @drink_id, 'WATER', 1, 2),
('Bia rượu', 'Bia, rượu các loại', @drink_id, 'ALCOHOL', 1, 3),
('Cà phê & trà', 'Cà phê, trà túi lọc, trà đá', @drink_id, 'COFFEE', 1, 4),
('Nước ép', 'Nước ép trái cây đóng hộp', @drink_id, 'JUICE', 1, 5);

-- Insert sub-categories for Bánh kẹo
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Bánh quy', 'Bánh quy các loại, cracker', @sweet_id, 'BISCUIT', 1, 1),
('Kẹo', 'Kẹo cứng, kẹo mềm, socola', @sweet_id, 'CANDY', 1, 2),
('Snack', 'Snack khoai tây, bánh tráng', @sweet_id, 'SNACK', 1, 3),
('Bánh mì', 'Bánh mì tươi, bánh sandwich', @sweet_id, 'BREAD', 1, 4);

-- Insert sub-categories for Gia vị
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Gia vị nấu ăn', 'Muối, đường, tiêu, gia vị', @spice_id, 'COOKING', 1, 1),
('Nước mắm & tương ớt', 'Nước mắm, tương ớt, xì dầu', @spice_id, 'SAUCE', 1, 2),
('Dầu ăn', 'Dầu đậu nành, dầu oliu', @spice_id, 'OIL', 1, 3);

-- Insert sub-categories for Đồ gia dụng
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Vệ sinh nhà cửa', 'Nước lau sàn, bột giặt', @home_id, 'CLEAN', 1, 1),
('Đồ dùng nhà bếp', 'Chén bát, dao kéo, dụng cụ nấu ăn', @home_id, 'KITCHEN', 1, 2),
('Túi nilon & giấy', 'Túi nilon, giấy ăn, giấy vệ sinh', @home_id, 'PAPER', 1, 3);

-- Insert sub-categories for Sức khỏe & làm đẹp
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Thuốc & vitamin', 'Thuốc cảm cúm, vitamin tổng hợp', @health_id, 'MEDICINE', 1, 1),
('Mỹ phẩm', 'Kem dưỡng da, son môi, nước hoa', @health_id, 'COSMETIC', 1, 2),
('Vệ sinh cá nhân', 'Kem đánh răng, dầu gội, xà phòng', @health_id, 'PERSONAL', 1, 3);

-- Hiển thị kết quả
SELECT 
    c1.category_id,
    c1.category_name,
    c1.category_code,
    COALESCE(c2.category_name, 'ROOT') as parent_name,
    c1.display_order,
    c1.is_active
FROM category c1 
LEFT JOIN category c2 ON c1.parent_category_id = c2.category_id 
ORDER BY 
    COALESCE(c1.parent_category_id, c1.category_id),
    c1.display_order,
    c1.category_name; 