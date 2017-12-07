工具方法:
1.
变量声明
private static Pattern pattern;//作用在正则表达式
    private static Matcher matcher;//作用在匹配
//    private String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]'-";



方法:
static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    static int computeLineWidth(String message,int mtextSize,int letterSpacing){
        int defaultWidth=0;
        for(int i=0;i<message.length()-1;i++){
            defaultWidth+=letterSpacing+getEveryWordWidth(String.valueOf(message.charAt(i)),mtextSize);
        }
        return defaultWidth;
    }

    static int getEveryWordWidth(String myword,int mtextSize){
        int everyWordWidth=0;
        pattern=Pattern.compile("[\u4e00-\u9fa5]");
        matcher=pattern.matcher(myword);
        if(matcher.matches()){
            everyWordWidth=mtextSize;
        }else {
            everyWordWidth=mtextSize/2;
        }
        return everyWordWidth;
    }


2.应用方法:控件循环添加并合成图片

 StringBuilder sb = new StringBuilder();
        for (int i = 0; i < printItemObjsSize; i++) {
            PrintItemObj printItemObj = printItemObjs.get(i);
            sb.append(printItemObj.getText()).append("\n");
            SDKLog.d(TAG, CommonFunction._FILE_LINE_FUN_() + "Text: " + printItemObj.getText());
        }
        String printText = sb.toString();
        LinearLayout linearLayout =new LinearLayout(TopwiseApplication.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(384, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        int tempLen = 0;
        for (int k = 0; k < printItemObjsSize; k++) {
            PrintItemObj printItemObj = printItemObjs.get(k);
            int fontSize;
            switch (printItemObj.getFontSize()) {
                case PrinterConstant.LKL_FONT_SIZE_SMALL:
                    fontSize = PrinterConstant.FONT_SIZE_SMALL;
                    break;
                case PrinterConstant.LKL_FONT_SIZE_MIDDLE:
                    fontSize = PrinterConstant.FONT_SIZE_MIDDLE;
                    break;
                case PrinterConstant.LKL_FONT_SIZE_LARGE:
                    fontSize = PrinterConstant.FONT_SIZE_LARGE;
                    break;
                case PrinterConstant.LKL_FONT_SIZE_SUPER_LARGE:
                    fontSize = PrinterConstant.FONT_SIZE_SUPER_LARGE;
                    break;
                default:
                    fontSize = PrinterConstant.FONT_SIZE_MIDDLE;
                    break;
            }

            //1 is the length of "\n"
            int textLen = printItemObj.getText().length() + 1;
            String TextMessage=printText.substring(tempLen, textLen + tempLen);//每一行的文本
            PrintTextView textView = new PrintTextView(TopwiseApplication.getContext());
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(PrinterConstant.PAPER_WIDTH,LinearLayout.LayoutParams.WRAP_CONTENT);
            tvParams.setMargins(0,printItemObj.getLineHeight()-26,0,0);
            textView.setLayoutParams(tvParams);
            //修饰开始
            switch (printItemObj.getAlign()) {
                case LEFT:
                    break;
                case CENTER:
                    textView.setViewLayout(Gravity.CENTER);
                    break;
                case RIGHT:
                    textView.setViewLayout(Gravity.RIGHT);
                    break;
                default:
                    break;
            }
            textView.setSpacing(printItemObj.getLetterSpacing());
            textView.setLetterSize(fontSize);
            textView.setCustomTypeFonts(TopwiseApplication.getTypeface());
            textView.setIsLetterUnderLine(printItemObj.isUnderline());
            textView.setCustomText(TextMessage);
            linearLayout.addView(textView);
            tempLen += textLen;
        }
        linearLayout.setDrawingCacheEnabled(true);
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        linearLayout.layout(0, 0, linearLayout.getMeasuredWidth(), linearLayout.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(PrinterConstant.PAPER_WIDTH, linearLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        linearLayout.draw(canvas);

        printBitmap(aidlPrinterListener, bitmap);
    
