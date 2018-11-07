package com.yunbaba.freighthelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

@Table("addrbean")
public class AddressBean implements Parcelable {

		@PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id")
    public long id;

	@Column("_kcode")
	public String kcode;
	@Column("_address")
	public String address;
	@Column("_uploadAddress")
	public String uploadAddress;
	@Column("_pcd")
	public String pcd;
	@Column("_latitude")
	public double latitude;
	@Column("_longitude")
	public double longitude;

//	public String getKcode() {
//		return kcode;
//	}
//
//	public void setKcode(String kcode) {
//		this.kcode = kcode;
//	}
//
//	public String getAddress() {
//		return address;
//	}
//
//	public void setAddress(String address) {
//		this.address = address;
//	}



	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(kcode);
		dest.writeString(address);
		dest.writeString(uploadAddress);
		dest.writeString(pcd);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}

	public static final Parcelable.Creator<AddressBean> CREATOR = new Creator<AddressBean>() {

		@Override
		public AddressBean createFromParcel(Parcel source) {

			AddressBean mPerson = new AddressBean();
			mPerson.kcode = source.readString();
			mPerson.address = source.readString();
			mPerson.uploadAddress = source.readString();
			mPerson.pcd = source.readString();
			mPerson.latitude = source.readDouble();
			mPerson.longitude = source.readDouble();
			return mPerson;
		}

		@Override
		public AddressBean[] newArray(int size) {

			return new AddressBean[size];
		}
	};

}
