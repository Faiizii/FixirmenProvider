package com.fixirman.provider.model.request;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Entity
public class RequestDetail implements Parcelable
{

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("worker_id")
    @Expose
    private Integer workerId;
    @SerializedName("initial_cost")
    @Expose
    private Double initialCost;
    @SerializedName("final_cost")
    @Expose
    private Double finalCost;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time_slot")
    @Expose
    private String timeSlot;
    @SerializedName("service_type")
    @Expose
    private Integer serviceType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("coupon_id")
    @Expose
    private Integer couponId;
    @SerializedName("coupon_discount")
    @Expose
    private Double couponDiscount;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("is_paid")
    @Expose
    private String isPaid;
    @SerializedName("address_id")
    @Expose
    private Integer addressId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("address_info")
    @Expose
    private String addressInfo;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("user_phone")
    @Expose
    private String userPhone;
    @SerializedName("user_rating")
    @Expose
    private Integer userRating;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
    @SerializedName("provider_image")
    @Expose
    private String providerImage;
    @SerializedName("provider_phone")
    @Expose
    private String providerPhone;
    @SerializedName("provider_description")
    @Expose
    private String providerDescription;
    @SerializedName("provider_rating")
    @Expose
    private Integer providerRating;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("category_image")
    @Expose
    private String categoryImage;
    @SerializedName("service_type_name")
    @Expose
    private String serviceTypeName;
    @SerializedName("service_type_image")
    @Expose
    private String serviceTypeImage;
    @SerializedName("service_type_factor")
    @Expose
    private Double serviceTypeFactor;
    @Ignore
    @SerializedName("services")
    @Expose
    private List<RequestService> services = null;
    @Ignore
    @SerializedName("bidders")
    @Expose
    private List<RequestBid> bidders = null;
    public final static Parcelable.Creator<RequestDetail> CREATOR = new Creator<RequestDetail>() {


        @SuppressWarnings({
                "unchecked"
        })
        public RequestDetail createFromParcel(Parcel in) {
            return new RequestDetail(in);
        }

        public RequestDetail[] newArray(int size) {
            return (new RequestDetail[size]);
        }

    }
            ;

    protected RequestDetail(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.workerId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.initialCost = ((Double) in.readValue((Double.class.getClassLoader())));
        this.finalCost = ((Double) in.readValue((Double.class.getClassLoader())));
        this.categoryId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.timeSlot = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceType = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.couponId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.couponDiscount = ((Double) in.readValue((Double.class.getClassLoader())));
        this.paymentMethod = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.isPaid = ((String) in.readValue((String.class.getClassLoader())));
        this.addressId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
        this.addressInfo = ((String) in.readValue((String.class.getClassLoader())));
        this.createdBy = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
        this.userImage = ((String) in.readValue((String.class.getClassLoader())));
        this.userPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.userRating = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.providerName = ((String) in.readValue((String.class.getClassLoader())));
        this.providerImage = ((String) in.readValue((String.class.getClassLoader())));
        this.providerPhone = ((String) in.readValue((String.class.getClassLoader())));
        this.providerDescription = ((String) in.readValue((String.class.getClassLoader())));
        this.providerRating = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.categoryName = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryImage = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceTypeName = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceTypeImage = ((String) in.readValue((String.class.getClassLoader())));
        this.serviceTypeFactor = ((Double) in.readValue((Double.class.getClassLoader())));
        in.readList(this.services, (RequestService.class.getClassLoader()));
        in.readList(this.bidders, (RequestBid.class.getClassLoader()));
    }

    public RequestDetail() {
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public Double getInitialCost() {
        return initialCost;
    }

    public void setInitialCost(Double initialCost) {
        this.initialCost = initialCost;
    }

    public Double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(Double finalCost) {
        this.finalCost = finalCost;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(Double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(String addressInfo) {
        this.addressInfo = addressInfo;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderImage() {
        return providerImage;
    }

    public void setProviderImage(String providerImage) {
        this.providerImage = providerImage;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getProviderDescription() {
        return providerDescription;
    }

    public void setProviderDescription(String providerDescription) {
        this.providerDescription = providerDescription;
    }

    public Integer getProviderRating() {
        return providerRating;
    }

    public void setProviderRating(Integer providerRating) {
        this.providerRating = providerRating;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getServiceTypeImage() {
        return serviceTypeImage;
    }

    public void setServiceTypeImage(String serviceTypeImage) {
        this.serviceTypeImage = serviceTypeImage;
    }

    public Double getServiceTypeFactor() {
        return serviceTypeFactor;
    }

    public void setServiceTypeFactor(Double serviceTypeFactor) {
        this.serviceTypeFactor = serviceTypeFactor;
    }

    public List<RequestService> getServices() {
        return services;
    }

    public void setServices(List<RequestService> services) {
        this.services = services;
    }

    public List<RequestBid> getBidders() {
        return bidders;
    }

    public void setBidders(List<RequestBid> bidders) {
        this.bidders = bidders;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(workerId);
        dest.writeValue(initialCost);
        dest.writeValue(finalCost);
        dest.writeValue(categoryId);
        dest.writeValue(description);
        dest.writeValue(date);
        dest.writeValue(timeSlot);
        dest.writeValue(serviceType);
        dest.writeValue(couponId);
        dest.writeValue(couponDiscount);
        dest.writeValue(paymentMethod);
        dest.writeValue(status);
        dest.writeValue(isPaid);
        dest.writeValue(addressId);
        dest.writeValue(address);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
        dest.writeValue(addressInfo);
        dest.writeValue(createdBy);
        dest.writeValue(createDate);
        dest.writeValue(updateDate);
        dest.writeValue(userName);
        dest.writeValue(userImage);
        dest.writeValue(userPhone);
        dest.writeValue(userRating);
        dest.writeValue(providerName);
        dest.writeValue(providerImage);
        dest.writeValue(providerPhone);
        dest.writeValue(providerDescription);
        dest.writeValue(providerRating);
        dest.writeValue(categoryName);
        dest.writeValue(categoryImage);
        dest.writeValue(serviceTypeName);
        dest.writeValue(serviceTypeImage);
        dest.writeValue(serviceTypeFactor);
        dest.writeList(services);
        dest.writeList(bidders);
    }

    public int describeContents() {
        return 0;
    }

}