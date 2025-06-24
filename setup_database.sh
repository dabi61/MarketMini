#!/bin/bash

# ===========================================
# MARKETMINI DATABASE SETUP SCRIPT
# ===========================================
# Tự động import toàn bộ database MarketMini

echo "=========================================="
echo "🚀 MARKETMINI DATABASE SETUP"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Kiểm tra MySQL có sẵn không
if ! command -v mysql &> /dev/null; then
    echo -e "${RED}❌ MySQL không được tìm thấy. Vui lòng cài đặt MySQL trước.${NC}"
    exit 1
fi

echo -e "${BLUE}📋 Database MarketMini sẽ được tạo mới hoàn toàn${NC}"
echo -e "${YELLOW}⚠️  Lưu ý: Database cũ (nếu có) sẽ bị xóa!${NC}"
echo ""

# Nhập thông tin MySQL
read -p "MySQL Username [root]: " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -s -p "MySQL Password: " MYSQL_PASS
echo ""

read -p "MySQL Host [localhost]: " MYSQL_HOST  
MYSQL_HOST=${MYSQL_HOST:-localhost}

read -p "MySQL Port [3306]: " MYSQL_PORT
MYSQL_PORT=${MYSQL_PORT:-3306}

echo ""
echo -e "${BLUE}🔄 Bắt đầu import database...${NC}"

# Kiểm tra kết nối MySQL
if ! mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASS" -e "SELECT 1;" &> /dev/null; then
    echo -e "${RED}❌ Không thể kết nối MySQL. Kiểm tra lại thông tin.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Kết nối MySQL thành công${NC}"

# Import files theo thứ tự
FILES=("COMPLETE_DATABASE.sql" "SAMPLE_DATA.sql" "PROCEDURES_VIEWS.sql")
DESCRIPTIONS=("Cấu trúc database" "Dữ liệu mẫu" "Views & Procedures")

for i in "${!FILES[@]}"; do
    FILE="${FILES[$i]}"
    DESC="${DESCRIPTIONS[$i]}"
    
    echo ""
    echo -e "${BLUE}📁 Import ${DESC}: ${FILE}${NC}"
    
    if [ ! -f "$FILE" ]; then
        echo -e "${RED}❌ File $FILE không tồn tại${NC}"
        exit 1
    fi
    
    if mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASS" < "$FILE"; then
        echo -e "${GREEN}✅ Import $FILE thành công${NC}"
    else
        echo -e "${RED}❌ Lỗi import $FILE${NC}"
        exit 1
    fi
done

echo ""
echo -e "${BLUE}🔍 Kiểm tra dữ liệu đã import...${NC}"

# Verify import success
VERIFICATION_SQL="
USE marketmini;
SELECT 'Employees' as table_name, COUNT(*) as record_count FROM employees
UNION ALL SELECT 'Categories', COUNT(*) FROM category  
UNION ALL SELECT 'Products', COUNT(*) FROM products
UNION ALL SELECT 'Work Shifts', COUNT(*) FROM workshifts
UNION ALL SELECT 'Orders', COUNT(*) FROM orders;
"

echo "$VERIFICATION_SQL" | mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASS" -t

echo ""
echo -e "${GREEN}🎉 DATABASE SETUP HOÀN TẤT!${NC}"
echo ""
echo -e "${YELLOW}📋 THÔNG TIN TÀI KHOẢN:${NC}"
echo -e "${BLUE}👑 Admin Account:${NC}"
echo "   Username: admin1"
echo "   Password: pass1"
echo "   Role: 1 (Full Access)"
echo ""
echo -e "${BLUE}👥 Staff Accounts:${NC}"
echo "   baphu/1, linhstaff/linh123, ducstaff/1234"
echo "   maistaff/mai456, hungstaff/hung789"
echo "   Role: 2 (Staff Access)"
echo ""
echo -e "${YELLOW}🧪 TEST SCENARIOS:${NC}"
echo "• Ca làm hiện tại: admin1 & baphu đang trong ca 25/06/2025"
echo "• Chốt ca: Login staff account → Thống kê → Nút 'Chốt ca'"  
echo "• Dashboard admin: Login admin1 → Quản lý ca làm đầy đủ"
echo "• Dữ liệu: 15+ orders, 24 workshifts, full inventory"
echo ""
echo -e "${GREEN}✨ Hệ thống sẵn sàng sử dụng!${NC}" 