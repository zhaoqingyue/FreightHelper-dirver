package com.yunbaba.freighthelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactsInfo implements Parcelable {
	private String mFirst;
	private String mName;
	private String mLetters;
	private String mTeam;
	private String mPhone;
	
	public ContactsInfo() {
		mFirst = "";
		mName = "";
		mLetters = "";
		mTeam = "";
		mPhone = "";
	}
	
	public ContactsInfo(ContactsInfo contactsInfo) {
		mFirst = contactsInfo.mFirst;
		mName = contactsInfo.mName;
		mLetters = contactsInfo.mLetters;
		mTeam = contactsInfo.mTeam;
		mPhone = contactsInfo.mPhone;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mFirst);
		out.writeString(mName);
		out.writeString(mLetters);
		out.writeString(mTeam);
		out.writeString(mPhone);
	}

	public static final Parcelable.Creator<ContactsInfo> CREATOR = new Parcelable.Creator<ContactsInfo>() {
		public ContactsInfo createFromParcel(Parcel in) {
			return new ContactsInfo(in);
		}

		public ContactsInfo[] newArray(int size) {
			return new ContactsInfo[size];
		}
	};

	private ContactsInfo(Parcel in) {
		mFirst = in.readString();
		mName = in.readString();
		mLetters = in.readString();
		mTeam = in.readString();
		mPhone = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	public String getFirst() {
		return mFirst;
	}

	public void setFirst(String first) {
		mFirst = first;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getLetters() {
		return mLetters;
	}

	public void setLetters(String letters) {
		mLetters = letters;
	}

	public String getTeam() {
		return mTeam;
	}

	public void setTeam(String team) {
		mTeam = team;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		mPhone = phone;
	}
}
