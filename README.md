# SelectLocalResourceTest
封装的一个简单的Glide4.4的工具类

占位符与加载错误已经封装好，不用继续设置

直接调用：

GlideUtils.load(this, list.get(0), ima1, options);

需要圆角时，解决centerCrop()与bitmapTransform圆角冲突

 RequestOptions options = new RequestOptions()
                .centerCrop()
                .bitmapTransform(GlideUtils.getRoundOptions(context, 30));//圆角
                
选取图片与视频：

SelectPicVidUtils.create(MainActivity.this)
                        .selectDatas(5, Constant.Files_Type_image, REQUEST_Q);
                        
  selectDatas（选取图片/视频最大数量，文件类型，Intent的REQUESTCODE）
  
  文件类型：Constant.Files_Type_image 图片
  
           Constant.Files_Type_video 视频
