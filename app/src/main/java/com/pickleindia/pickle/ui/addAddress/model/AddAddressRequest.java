package com.pickleindia.pickle.ui.addAddress.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 🔥 MVP-I Architecture and Android Jetpack 🔥
 * 🍴 Focused on Clean Architecture
 * Created by 🔱 Pratik Kataria 🔱 on 19-01-2022.
 */
public class AddAddressRequest implements Serializable {
    @Expose
    @SerializedName("data")
    private ArrayList<Data> data;

    public ArrayList<AddAddressRequest.Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public static class Data implements Serializable {
        @Expose
        @SerializedName("Name")
        private String Name;
        @Expose
        @SerializedName("mobileNo")
        private String mobileNo;
        @Expose
        @SerializedName("landmark")
        private String landmark;
        @Expose
        @SerializedName("areaPin")
        private String areaPin;
        @Expose
        @SerializedName("address")
        private String address;

        @SerializedName("id")
        private String id;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getAreaPin() {
            return areaPin;
        }

        public void setAreaPin(String areaPin) {
            this.areaPin = areaPin;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return  "Name:'" + Name + '\'' + "\n\n"+
                    "MobileNo: '" + mobileNo + '\'' + "\n\n"+
                    "Landmark: '" + landmark + '\'' + "\n\n"+
                    "AreaPin: '" + areaPin + '\'' + "\n\n"+
                    "Address: '" + address + '\'';
        }
    }
}

