package com.android.topwise.lklusdkservice.binder.printer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.android.topwise.sdk.utils.SDKLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PrintTextView extends TextView {
    private static final String TAG = "PrintTextView";
    private String content;
    private Paint paint;
    private int letterspacing;//字符间距
    private Typeface mTf;//行间距
    private int mtextSize;//字体大小
    private boolean mIsLetterUnderLine;//是否每个字符有下划线
    private int mViewLayout;//修改View对齐方式
    private boolean isCenter=false;//判断是否居中显示
    private boolean isRight=false;//判断是否居右显示
    private int LeftMarginser=0;//离左边距的距离
    private int mContentWidth=0;//本次字符的长度
    private boolean isEndText=false;//标记文本是否为结束文本
    public PrintTextView(Context context) {
        super(context);
        init();
    }
    public void setIsEndText(boolean TextEnd){
            isEndText = TextEnd;
    }
    public void setSpacing(int spacing){
        letterspacing=spacing;
    }

    public void setCustomTypeFonts(Typeface tf){
        mTf=tf;
    }

    public void setLetterSize(int lettertextsize){
        mtextSize=lettertextsize;
    }

    public void setIsLetterUnderLine(boolean IsLetterUnderLine){
        mIsLetterUnderLine=IsLetterUnderLine;
    }

    public void setCustomText(String MyText){
        content=MyText;
    }

    public void setViewLayout(int layout){
        mViewLayout=layout;
        switch (layout){
            case Gravity.CENTER:
                isCenter=true;
                isRight=false;
                break;
            case Gravity.RIGHT:
                isCenter=false;
                isRight=true;
                break;
            default:
                isCenter=false;
                isRight=false;
                break;
        }


    }


    public void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
    }

    //处理如果字符串之超出做大宽度的时候
    List<String> Indexes=new ArrayList<String>();//换行拆分字符串集合
    public List<String> SelectIndex(String Message,int mtextSize,int letterspacing){
        int IndexWidth=0;
        int TempIndex=0;
        Indexes.clear();
       for(int i=0;i<Message.length();i++){
           if(i == Message.length()-1 && IndexWidth<PrinterConstant.PAPER_WIDTH){
               Indexes.add(Message.substring(TempIndex,i));
           }
           String charString=String.valueOf(Message.charAt(i));
           IndexWidth+=BitmapUtil.getEveryWordWidth(charString,mtextSize);
           if(charString.equals("\n")){
               if(Indexes.size() == 0){
                   Indexes.add(Message.substring(0,i));
                   SDKLog.e(TAG,"String="+Message.substring(0,i));
                   TempIndex=i;
                   IndexWidth=0;
               }else {
                   Indexes.add(Message.substring(TempIndex,i));
                   SDKLog.e(TAG,"String="+Message.substring(TempIndex,i));
                   TempIndex=i;
                   IndexWidth=0;
               }
           }
           if(IndexWidth>PrinterConstant.PAPER_WIDTH){
             if(Indexes.size() == 0){
                 Indexes.add(Message.substring(0,i));
                 SDKLog.e(TAG,"dfg="+Message.substring(0,i));
                 TempIndex=i;
                 IndexWidth=0;
             }else {
                 Indexes.add(Message.substring(TempIndex,i));
                 SDKLog.e(TAG,"dfg="+Message.substring(TempIndex,i));
                 TempIndex=i;
                 IndexWidth=0;
             }
           }
           IndexWidth+=letterspacing;
       }
       return removeDuplicateWithOrder(Indexes);
    }

    public static List removeDuplicateWithOrder(List list) {
        Set set = new HashSet();
        List newList = new ArrayList();
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Object element = iter.next();
            if (set.add(element) || element.equals("\n"))
                newList.add(element);
        }
        return newList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        content=getText().toString();
        paint.setTypeface(mTf);
        paint.setAntiAlias(false);
        paint.setHinting(Paint.HINTING_OFF);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setTextSize(mtextSize);
        int LineNums=1;
        mContentWidth=BitmapUtil.computeLineWidth(content,mtextSize,letterspacing);
        if(mViewLayout == Gravity.CENTER){
            LeftMarginser=(PrinterConstant.PAPER_WIDTH+mtextSize-mContentWidth)/2;
        }else if(mViewLayout == Gravity.RIGHT){
            LeftMarginser=PrinterConstant.PAPER_WIDTH-mContentWidth;
        }
            //整体画布布局绘画(适配到多行)
            List<String> arrays=SelectIndex(content,mtextSize,letterspacing);
            for(int j=0;j<arrays.size();j++){
                LineNums=j+1;
                if (!TextUtils.isEmpty(arrays.get(j))) {
                    int Linediedai=0;
                    for (int i = 0; i < arrays.get(j).length(); i++) {
                        String worder=String.valueOf(arrays.get(j).charAt(i));
                        if(worder.equals("\n")){continue;}
                        int everyWordWidth=BitmapUtil.getEveryWordWidth(worder,mtextSize);
                        canvas.drawText(worder, Linediedai/*i*(letterspacing+mtextSize)*/+LeftMarginser,(mtextSize)*LineNums, paint);
                        if(mIsLetterUnderLine){
                            canvas.drawLine(Linediedai/*i*(letterspacing+mtextSize)*/+LeftMarginser,(mtextSize+2)*LineNums,Linediedai/*i*(letterspacing+mtextSize)*/+everyWordWidth+LeftMarginser,(mtextSize+2)*LineNums,paint);
                        }
                        Linediedai+=everyWordWidth+letterspacing;
                    }
                }
            }
            LeftMarginser=0;
        }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int Textheight=mtextSize+4;
        int mLineTextHeight=Textheight*SelectIndex(content,mtextSize,letterspacing).size();
        setMeasuredDimension(widthMeasureSpec,mLineTextHeight);
    }
}