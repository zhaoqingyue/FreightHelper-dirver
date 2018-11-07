package com.yunbaba.freighthelper.utils;

import java.util.Comparator;

import com.yunbaba.freighthelper.bean.ContactsInfo;

public class PinyinComparator implements Comparator<ContactsInfo> {

	public int compare(ContactsInfo o1, ContactsInfo o2) {
		if (o1.getLetters().equals("@") || o2.getLetters().equals("#")) {
			return -1;
		} else if (o1.getLetters().equals("#") || o2.getLetters().equals("@")) {
			return 1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}
}
