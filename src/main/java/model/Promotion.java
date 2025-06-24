package model;

import java.util.Date;

public class Promotion {
    private int promotionId;
    private String promotionName;
    private Date startDate;
    private Date endDate;
    private String productName; // dùng khi join với bảng product
    private int productId;      // <-- thêm dòng này
    private int discount;
    private int originalPrice;
    private int discountedPrice;

    // Constructors
    public Promotion() {}
    
    public Promotion(int promotionId, String promotionName, Date startDate, Date endDate,
                     String productName, int productId, int discount, int originalPrice, int discountedPrice) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.productName = productName;
        this.productId = productId;
        this.discount = discount;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
    }

    // Getters and Setters
    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
