/*
 * @Title CldOlsModel.java
 * @Copyright Copyright 2010-2015 Careland Software Co,.Ltd All Rights Reserved.
 * @author Zhouls
 * @date 2015-11-30 上午9:37:20
 * @version 1.0
 */
package com.yunbaba.ols.tools.model;

/**
 * 模块初始化模型
 * 
 * @author Zhouls
 * @date 2015-11-30 上午9:37:20
 */
public abstract class CldOlsInitMod {

	/**
	 * 初始化
	 * 
	 * @param param
	 *            初始化参数，若无则填null
	 * @return 错误码（0：成功，其它：各模块定义）
	 * @return int
	 * @author Wenap
	 * @date 2015-6-1 上午10:52:02
	 */
	public abstract int init(Object param);

	/**
	 * 初始化在线部分耗时数据
	 * 
	 * @return void
	 * @author Zhouls
	 * @date 2016-1-19 下午5:36:32
	 */
	public abstract void initOnlineData(Object param);

	/**
	 * 调用模块需要实现的接口
	 * 
	 * @author Wenap
	 * @date 2015-6-4 上午11:38:15
	 */
	public abstract interface ICldOlsModelInterface {

		/**
		 * 获取模块初始化参数对象，若模块需要实现
		 * 
		 * @return 初始化参数
		 * @return Object
		 * @author Wenap
		 * @date 2015-6-4 上午11:36:33
		 */
		public abstract Object getInitParams();

	}

	/**
	 * 获取模块类名
	 * 
	 * @return 模块类名
	 * @return String
	 * @author Wenap
	 * @date 2015-6-10 上午11:32:11
	 */
	public String getModelName() {
		return this.getClass().getName();
	}

	/**
	 * 是否已经初始化，模块初始化、反初始化时需要判断
	 */
	protected boolean bInit = false;

	/**
	 * 模块接口
	 */
	public ICldOlsModelInterface mInterface = null;
}
