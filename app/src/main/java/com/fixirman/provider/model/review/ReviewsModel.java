package com.fixirman.provider.model.review;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class ReviewsModel implements Parcelable
{

    @NonNull
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("request_id")
    @Expose
    private Integer requestId;
    @SerializedName("star")
    @Expose
    private Integer star;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("rated_by")
    @Expose
    private Integer ratedBy;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("by_name")
    @Expose
    private String byName;
    @SerializedName("by_image")
    @Expose
    private String byImage;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    public final static Parcelable.Creator<ReviewsModel> CREATOR = new Creator<ReviewsModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ReviewsModel createFromParcel(Parcel in) {
            return new ReviewsModel(in);
        }

        public ReviewsModel[] newArray(int size) {
            return (new ReviewsModel[size]);
        }

    }
            ;

    protected ReviewsModel(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.userId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.categoryId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.requestId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.star = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.feedback = ((String) in.readValue((String.class.getClassLoader())));
        this.ratedBy = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.createDate = ((String) in.readValue((String.class.getClassLoader())));
        this.updateDate = ((String) in.readValue((String.class.getClassLoader())));
        this.byName = ((String) in.readValue((String.class.getClassLoader())));
        this.byImage = ((String) in.readValue((String.class.getClassLoader())));
        this.categoryName = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ReviewsModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(Integer ratedBy) {
        this.ratedBy = ratedBy;
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

    public String getByName() {
        return byName;
    }

    public void setByName(String byName) {
        this.byName = byName;
    }

    public String getByImage() {
        return byImage;
    }

    public void setByImage(String byImage) {
        this.byImage = byImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userId);
        dest.writeValue(categoryId);
        dest.writeValue(requestId);
        dest.writeValue(star);
        dest.writeValue(feedback);
        dest.writeValue(ratedBy);
        dest.writeValue(createDate);
        dest.writeValue(updateDate);
        dest.writeValue(byName);
        dest.writeValue(byImage);
        dest.writeValue(categoryName);
    }

    public int describeContents() {
        return 0;
    }

}