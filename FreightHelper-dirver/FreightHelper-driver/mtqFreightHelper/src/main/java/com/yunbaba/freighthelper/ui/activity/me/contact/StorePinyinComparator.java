package com.yunbaba.freighthelper.ui.activity.me.contact;

import java.util.Comparator;

import com.yunbaba.ols.module.delivery.bean.MtqStore;

public class StorePinyinComparator implements Comparator<MtqStore> {



	@Override
	public int compare(MtqStore o1, MtqStore o2) {

		if (o1.getLetter().equals("@") || o2.getLetter().equals("#")) {
			return -1;
		} else if (o1.getLetter().equals("#") || o2.getLetter().equals("@")) {
			return 1;
		} else {
			return o1.getLetter().compareTo(o2.getLetter());
		}
	}
}

