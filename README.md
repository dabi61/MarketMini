# MarketMini Database v2.0 🏪

**Database hoàn chỉnh cho hệ thống quản lý siêu thị mini với tính năng quản lý ca làm việc**

## 🚀 Quick Setup Database

### Auto Setup (Khuyến nghị)
```bash
chmod +x setup_database.sh
./setup_database.sh
```

### Manual Setup
```bash
mysql -u root -p < COMPLETE_DATABASE.sql
mysql -u root -p < SAMPLE_DATA.sql  
mysql -u root -p < PROCEDURES_VIEWS.sql
```

## 🗂️ Database Files

| File | Mô tả |
|------|-------|
| `COMPLETE_DATABASE.sql` | ⚡ Cấu trúc database (15 bảng + indexes) |
| `SAMPLE_DATA.sql` | 📊 Dữ liệu mẫu đầy đủ |
| `PROCEDURES_VIEWS.sql` | 🔧 Views, procedures, functions |
| `setup_database.sh` | 🤖 Script tự động setup |

## 👥 Tài Khoản Test

**Admin**: `admin1` / `pass1` (role=1)  
**Staff**: `baphu` / `1`, `linhstaff` / `linh123` (role=2)

## ✨ Tính Năng Mới v2.0

- ✅ **Quản lý ca làm việc** (workshifts table)
- ✅ **Dashboard phân quyền** (admin vs staff)  
- ✅ **Chốt ca tự động** với tính lương overtime
- ✅ **Thống kê chi tiết** (views & analytics)
- ✅ **Category hierarchy** (danh mục cha-con)
- ✅ **Auto triggers** (stock, points, etc.)

## 📊 Dữ Liệu Có Sẵn

- 👥 **6 nhân viên** (1 admin, 5 staff)
- 📦 **15 sản phẩm** (dầu gội, dầu ăn, sữa tươi)
- 🛒 **15 đơn hàng** (từ 17-24/06/2025)
- ⏰ **24 ca làm việc** (bao gồm ca đang diễn ra)
- 🏪 **5 nhà cung cấp** + full inventory

## 🧪 Test Scenarios

1. **Login admin1** → Dashboard quản lý ca làm đầy đủ
2. **Login staff** → Dashboard riêng + nút "Chốt ca"  
3. **Test chốt ca**: Staff có ca `IN_PROGRESS` ngày 25/06/2025
4. **Thống kê**: Views với dữ liệu realistic

## 📋 Database Schema

```
marketmini (15 tables)
├── employees ✅         # Nhân viên + roles
├── category ✅          # Danh mục có hierarchy  
├── products ✅          # Sản phẩm với stock
├── customers ✅         # Khách hàng có points
├── suppliers ✅         # Nhà cung cấp
├── workshifts 🆕        # Ca làm việc 
├── workingsession ✅    # Phiên làm việc
├── orders ✅            # Đơn hàng
├── orderdetails ✅      # Chi tiết đơn hàng
├── imports ✅           # Nhập hàng
├── promotion ✅         # Khuyến mãi
├── productdisplay ✅    # Hiển thị sản phẩm
├── monthly_expenses ✅  # Chi phí tháng
├── invoices ✅          # Hóa đơn
└── salary ✅            # Lương nhân viên
```

## 🔧 Advanced Features

### Views & Analytics
- `v_employee_shift_summary` - Thống kê ca làm
- `v_product_sales_summary` - Thống kê bán hàng
- `v_inventory_status` - Tình trạng kho

### Stored Procedures  
- `CloseWorkShift()` - Chốt ca làm việc
- `StartWorkShift()` - Bắt đầu ca làm
- `CreateBulkShifts()` - Tạo ca hàng loạt

### Functions
- `CalculateShiftEarnings()` - Tính lương theo ca
- `CalculateAttendanceRate()` - Tỷ lệ chuyên cần

### Auto Triggers
- Stock update khi bán/nhập hàng
- Tính điểm thưởng khách hàng tự động

---

🎯 **Database đã sẵn sàng!** Import và test ngay với đầy đủ tính năng quản lý ca làm việc.

📚 Xem `DATABASE_SETUP_INSTRUCTIONS.md` để biết chi tiết.
