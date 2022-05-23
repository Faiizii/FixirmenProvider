
package com.fixirman.provider.model.review;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse implements Parcelable
{

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("reviews")
    @Expose
    private List<ReviewsModel> reviews = null;
    public final static Creator<ReviewResponse> CREATOR = new Creator<ReviewResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ReviewResponse createFromParcel(Parcel in) {
            return new ReviewResponse(in);
        }

        public ReviewResponse[] newArray(int size) {
            return (new ReviewResponse[size]);
        }

    }
    ;

    protected ReviewResponse(Parcel in) {
        this.success = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.reviews, (ReviewsModel.class.getClassLoader()));
    }

    public ReviewResponse() {
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ReviewsModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewsModel> reviews) {
        this.reviews = reviews;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(success);
        dest.writeValue(message);
        dest.writeList(reviews);
    }

    public int describeContents() {
        return  0;
    }

}
