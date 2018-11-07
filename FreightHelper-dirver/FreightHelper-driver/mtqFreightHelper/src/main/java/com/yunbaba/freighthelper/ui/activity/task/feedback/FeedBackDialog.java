package com.yunbaba.freighthelper.ui.activity.task.feedback;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cld.device.CldPhoneNet;
import com.cld.nv.map.CldMapApi;
import com.yunbaba.freighthelper.MainApplication;
import com.yunbaba.freighthelper.R;
import com.yunbaba.freighthelper.ui.activity.task.ImagePagerActivity;
import com.yunbaba.freighthelper.ui.adapter.PicGridViewAdapter;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog;
import com.yunbaba.freighthelper.ui.customview.SimpleIndexSelectDialog.OnSimpleIndexSelectCallBack;
import com.yunbaba.freighthelper.utils.FileUtils;
import com.yunbaba.freighthelper.utils.ImageTools;
import com.yunbaba.freighthelper.utils.PermissionUtil;
import com.yunbaba.freighthelper.utils.WaitingProgressTool;
import com.yunbaba.ols.module.delivery.CldBllKDelivery;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI;
import com.yunbaba.ols.module.delivery.CldKDeliveryAPI.IUploadPicListener;
import com.yunbaba.ols.module.delivery.CldSapKDeliveryParam.FeedBackInfo;
import com.yunbaba.ols.module.delivery.bean.MtqDeliOrderDetail;
import com.yunbaba.ols.module.delivery.bean.MtqDeliStoreDetail;
import com.yunbaba.ols.module.delivery.tool.CldKDeviceAPI;
import com.yunbaba.ols.tools.model.CldOlsInterface.ICldResultListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hmi.packages.HPDefine.HPWPoint;

public class FeedBackDialog extends Dialog implements android.view.View.OnClickListener {

    ArrayList<FeedBackInfo> reasonlist;

    @BindView(R.id.gv_reasonselect)
    GridView gv_reasonselect;
    @BindView(R.id.gv_pic)
    GridView gv_pic;

    // @BindView(R.id.tv_pic)
    // TextView tv_pic;

    @BindView(R.id.iv_close)
    ImageView iv_close;

    @BindView(R.id.et_remark)
    EditText et_remark;

    @BindView(R.id.tv_wordcount)
    TextView tv_wordcount;

    @BindView(R.id.tv_pic)
    TextView tvPicnumhint;

    @BindView(R.id.dialog_confiem_ok)
    Button dialog_confiem_ok;

    FeedBackReasonSelectAdapter selectAdapter;

    OnOkButtonClickListener onOkButtonClickListener;

    PicGridViewAdapter picAdapter;

    MtqDeliStoreDetail storeDetail;
    MtqDeliOrderDetail orderDetail;
    List<String> piclist;
    Activity mContext;
    int type; //反馈分类（1-为司机反馈；2-司机暂停）

    // 拍照
    public static final int IMAGE_CAPTURE = 9990;
    // 从相册选择
    public static final int IMAGE_SELECT = 9995;

    public interface IPromptDialogListener {

        public void OnCancel();

        public void OnSure();
    }

    public FeedBackDialog(Context context) {
        super(context);
    }

    public FeedBackDialog(Activity context, ArrayList<FeedBackInfo> list, MtqDeliStoreDetail storeDetail,
                          MtqDeliOrderDetail orderDetail, int type) {
        super(context, R.style.dialog_style);
        mContext = context;
        this.reasonlist = list;
        this.storeDetail = storeDetail;
        this.orderDetail = orderDetail;
        this.type = type;
    }

    // public PromptDialog(Context context, String messageStr, String cancelStr,
    // String sureStr, IPromptDialogListener listener) {
    // super(context, R.style.dialog_style);
    // mMessageStr = messageStr;
    // mCancelStr = cancelStr;
    // mSureStr = sureStr;
    // mListener = listener;
    // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 点击Dialog以外的区域，Dialog关闭
        setCanceledOnTouchOutside(false);
        getWindow().setContentView(R.layout.view_taskfeedback);
        ButterKnife.bind(this);
        initViews();
        setListener();

    }

    private void initViews() {

        piclist = new ArrayList<>();

        selectAdapter = new FeedBackReasonSelectAdapter(mContext, reasonlist,
                new FeedBackReasonSelectAdapter.OnReansonChangeListner() {

                    @Override
                    public void onChange() {


                        if (!TextUtils.isEmpty(selectAdapter.getSelectResult()))
                            dialog_confiem_ok.setEnabled(true);

                        checkState();

                    }
                });

        picAdapter = new PicGridViewAdapter(mContext, piclist, 3, 2, storeDetail.corpId, storeDetail.taskId);

        gv_reasonselect.setAdapter(selectAdapter);

        gv_pic.setAdapter(picAdapter);

        // gv_pic.setHro

        gv_pic.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == piclist.size())
                    selectPic(3);
                else {

                    modifyPic(position);

                }
            }
        });

    }

    public void checkState() {

        try {
            if (TextUtils.isEmpty(selectAdapter.getSelectResult()) && TextUtils.isEmpty(et_remark.getText())) {

                dialog_confiem_ok.setEnabled(false);

            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void setListener() {

        iv_close.setOnClickListener(this);
        dialog_confiem_ok.setOnClickListener(this);

        et_remark.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            // private int selectionStart;
            // private int selectionEnd;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                if (temp == null)
                    temp = "";

                if (!TextUtils.isDigitsOnly(temp))
                    dialog_confiem_ok.setEnabled(true);

                checkState();

            }

            public void afterTextChanged(Editable s) {

                tv_wordcount.setText(temp.length() + "/200");

            }
        });

    }

    @Override
    public void onClick(View v) {
        // this.dismiss();
        switch (v.getId()) {
            case R.id.iv_close: {
                dismiss();

                break;
            }
            case R.id.dialog_confiem_ok: {

                onOkButtonClickListener.onClick();

                // CldBllKDelivery.getInstance().uploadFeedBackInfo(storeDetail.corpId,
                // storeDetail.taskId, storeDetail.waybill,
                // order_id, point.x, point.y, content, remark, mediaid, new
                // ICldResultListener() {
                //
                // @Override
                // public void onGetResult(int errCode) {
                //
                //
                // }
                //
                // @Override
                // public void onGetReqKey(String tag) {
                //
                //
                // }
                // });
                //
                //
                //
                //
                //
                if (TextUtils.isEmpty(selectAdapter.getSelectResult()) && TextUtils.isEmpty(et_remark.getText())
                        ) {

                    Toast.makeText(mContext, "请至少选择一个标签或填写备注", Toast.LENGTH_SHORT).show();

                } else
                    UploadPic(picIndexs);

            }
            default:
                break;
        }
    }


    public void setOnOkButtonClickListener(OnOkButtonClickListener listener) {
        this.onOkButtonClickListener = listener;
    }

    public String[] picResponseIdlist = new String[3];

    int picIndexs = 0;

    public void UploadPic(final int picIndex) {

        // dismiss();

        picIndexs = picIndex;

        if (picIndex >= piclist.size()) {

            // Toast.makeText(mContext, "图片上传完成,开始提交信息",
            // Toast.LENGTH_SHORT).show();
            Toast.makeText(mContext, "正在提交", Toast.LENGTH_SHORT).show();
            // showProgressBar();
            // hideProgressBar();
            WaitingProgressTool.closeshowProgress();
            UploadInfo();

            return;
        }

        // showProgressBar();
        WaitingProgressTool.showProgress(mContext);

        String base64_pic = null;
        try {
            base64_pic = Base64.encodeToString(FileUtils.toByteArray(piclist.get(picIndex)), Base64.DEFAULT);

        } catch (IOException e) {


            e.printStackTrace();
            Toast.makeText(mContext, "图片解析失败", Toast.LENGTH_SHORT).show();
        }

        long time = CldKDeviceAPI.getSvrTime();

        CldKDeliveryAPI.getInstance().uploadPicture(storeDetail.corpId, time, 0, 0, base64_pic,
                new IUploadPicListener() {

                    @Override
                    public void onGetResult(int errCode, String mediaid) {

                        // if (isFinishing())
                        // return;

                        if (errCode == 0) {
                            picResponseIdlist[picIndex] = mediaid;
                            UploadPic((picIndex + 1));
                        } else {

                            Toast.makeText(mContext,

                                    "操作失败，请检查网络",
                                    // "图片上传失败，已上传" + (picIndex) + "张，未上传" +
                                    // (piclistpath.size() - picIndex) + "张",
                                    Toast.LENGTH_SHORT).show();

                            WaitingProgressTool.closeshowProgress();

                        }

                    }

                    @Override
                    public void onGetReqKey(String arg0) {


                    }
                });

    }

    public void UploadInfo() {

        if (!CldPhoneNet.isNetConnected()) {
            Toast.makeText(mContext, R.string.userinfo_set_failed, Toast.LENGTH_SHORT).show();
            WaitingProgressTool.closeshowProgress();
            return;
        }

        // WaitingProgressTool.showProgress(mContext);

        String remark = "";
        String content = "";

        if (!TextUtils.isEmpty(et_remark.getText()))
            remark = et_remark.getText().toString();

        content = selectAdapter.getSelectResult();

        String extpic = "";

        over:
        for (int i = 0; i < picResponseIdlist.length; i++) {

            String picid = picResponseIdlist[i];

            if (!TextUtils.isEmpty(picid)) {
                if (i != 0) {

                    extpic += "|";
                }
                extpic += picid;

            } else {
                break over;
            }

        }

        WaitingProgressTool.showProgress(mContext);
        HPWPoint point = CldMapApi.getMapCenter();

        CldBllKDelivery.getInstance().uploadFeedBackInfo(storeDetail.corpId, storeDetail.taskId, storeDetail.waybill,
                storeDetail.waybill, type, point.x, point.y, content, remark, extpic, new ICldResultListener() {

                    @Override
                    public void onGetResult(int errCode) {

                        WaitingProgressTool.closeshowProgress();

                        if (errCode != 0)
                            Toast.makeText(mContext, "操作失败，请检查网络。", Toast.LENGTH_SHORT).show();
                            // dismiss();
                        else {

                            Toast.makeText(mContext, "已提交", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }

                    @Override
                    public void onGetReqKey(String tag) {


                    }
                });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //MLog.e("onactiviyresasdsadsadsad", requestCode+" ----- "+resultCode);
        //Toast.makeText(mContext, , duration)
        if (resultCode == Activity.RESULT_OK && resultCode != Activity.RESULT_CANCELED) {


            String afterCompressPicPath;
            switch (requestCode) {
                case IMAGE_CAPTURE - 1:// 拍照返回

                    afterCompressPicPath = ImageTools.compress(mContext,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        piclist.add(afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclist);
                        // receiptlistpath.add(afterCompressPicPath); //
                        // afterCompressPicPath
                        // RefreshHint();
                        // receiptAdapter.setList(receiptlistpath);

                        dialog_confiem_ok.setEnabled(true);

                        checkState();
                    } else {
                        Toast.makeText(mContext, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case IMAGE_SELECT - 1:// 选择照片返回
                    // 照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {

                        afterCompressPicPath = null;

                        String fp = FileUtils.getRealFilePath(mContext, originalUri);

                        afterCompressPicPath = ImageTools.compress(mContext, fp);

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                            // receiptlistpath.add(afterCompressPicPath); //
                            // RefreshHint();
                            // receiptAdapter.setList(receiptlistpath);
                            piclist.add(afterCompressPicPath);
                            RefreshHint();
                            picAdapter.setList(piclist);

                            dialog_confiem_ok.setEnabled(true);

                            checkState();
                        } else {
                            Toast.makeText(mContext, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                case IMAGE_CAPTURE:
                case IMAGE_CAPTURE + 1:
                case IMAGE_CAPTURE + 2:

                    int pos = requestCode - IMAGE_CAPTURE;
                    afterCompressPicPath = ImageTools.compress(mContext,
                            MainApplication.getTmpCacheFilePath() + "/image.jpg");

                    if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {

                        if (pos < piclist.size()) {

                            piclist.set(pos, afterCompressPicPath);

                        }

                        // piclist.add(afterCompressPicPath);
                        RefreshHint();
                        picAdapter.setList(piclist);
                        // receiptlistpath.add(afterCompressPicPath); //
                        // afterCompressPicPath
                        // RefreshHint();
                        // receiptAdapter.setList(receiptlistpath);

                        dialog_confiem_ok.setEnabled(true);

                        checkState();
                    } else {
                        Toast.makeText(mContext, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case IMAGE_SELECT:
                case IMAGE_SELECT + 1:
                case IMAGE_SELECT + 2:
                    int pos2 = requestCode - IMAGE_SELECT;
                    Uri originalUri2 = data.getData();
                    try {

                        afterCompressPicPath = null;

                        String fp = FileUtils.getRealFilePath(mContext, originalUri2);

                        afterCompressPicPath = ImageTools.compress(mContext, fp);

                        if (afterCompressPicPath != null && !TextUtils.isEmpty(afterCompressPicPath)) {
                            // receiptlistpath.add(afterCompressPicPath); //
                            // RefreshHint();
                            // receiptAdapter.setList(receiptlistpath);
                            if (pos2 < piclist.size()) {

                                piclist.set(pos2, afterCompressPicPath);

                            }
                            RefreshHint();
                            picAdapter.setList(piclist);

                            dialog_confiem_ok.setEnabled(true);

                            checkState();
                        } else {
                            Toast.makeText(mContext, "获取图片失败,请检查设置是否允许储存空间权限", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public void RefreshHint() {

        if (piclist.size() > 0)
            tvPicnumhint.setVisibility(View.GONE);
        else {
            tvPicnumhint.setVisibility(View.VISIBLE);
            tvPicnumhint.setText("拍张参考照片,我们会优先处理哦~");

        }

    }

    /**
     * 选择图片
     */
    public void selectPic(final int type) {

        if (PermissionUtil.isNeedPermissionForStorage(mContext)) {

            Toast.makeText(mContext, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
            return;

        }

        SimpleIndexSelectDialog.ShowSelectDialog(mContext, new String[]{"拍照", "图库"}, true,
                new OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(mContext, Manifest.permission.CAMERA)) {

                                    Toast.makeText(mContext, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
                                    return;

                                }

                                captureImage(MainApplication.getTmpCacheFilePath(), -1);

                                break;
                            case 1:// 从图库选择
                                selectImage(-1);

                                break;

                            default:
                                break;
                        }
                    }
                });

    }

    /**
     * 更改，删除
     */
    public void modifyPic(final int position) {

        if (PermissionUtil.isNeedPermissionForStorage(mContext)) {

            Toast.makeText(mContext, "请打开储存空间权限", Toast.LENGTH_SHORT).show();
            return;

        }

        SimpleIndexSelectDialog.ShowSelectDialog(mContext, new String[]{"更改-拍照", "更改-图库", "查看", "删除"}, true,
                new OnSimpleIndexSelectCallBack() {

                    @Override
                    public void OnIndexSelect(int index, String select) {

                        switch (index) {
                            case 0:// 拍照

                                if (PermissionUtil.isNeedPermission(mContext, Manifest.permission.CAMERA)) {

                                    Toast.makeText(mContext, "请打开摄像头权限", Toast.LENGTH_SHORT).show();
                                    return;

                                }

                                captureImage(MainApplication.getTmpCacheFilePath(), position);

                                break;
                            case 1:// 从图库选择
                                selectImage(position);

                                break;
                            case 2:// 查看
                                imageBrower(position, (ArrayList<String>) piclist);
                                break;
                            case 3:// 删除

                                if (piclist != null && position < piclist.size()) {

                                    piclist.remove(position);
                                    picAdapter.setList(piclist);

                                }

                                checkState();

                                break;
                            default:
                                break;
                        }
                    }
                });

    }

    /**
     * 拍照
     *
     * @param path 照片存放的路径
     */
    public void captureImage(String path, int pos) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory("android.intent.category.DEFAULT");
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        Uri contentUri = FileUtils.getUriForFile(mContext,new File(path, "image.jpg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        mContext.startActivityForResult(intent, IMAGE_CAPTURE + pos);
    }

    /**
     * 从图库中选取图片
     */
    public void selectImage(int pos) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        // int pos = (position == -1)? 0:position+1;
        mContext.startActivityForResult(intent, IMAGE_SELECT + pos);
    }

    /**
     * 打开图片查看器
     *
     * @param position
     * @param urls2
     */
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        intent.putExtra("corpid", storeDetail.corpId);
        intent.putExtra("taskid", storeDetail.taskId);
        mContext.startActivity(intent);
    }

    public interface OnOkButtonClickListener {
        void onClick();
    }
}
