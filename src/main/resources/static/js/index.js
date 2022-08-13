$(function(){
	$("#publishBtn").click(publish);//获取按钮，定义单击事件，单击时调用publish方法
});

function publish() {
	$("#publishModal").modal("hide");//当点击发布时就将刚才填数据的对话框隐藏

	//获取标题和内容
	var title = $("#recipient-name").val();//$("#recipient-name")-->id选择器,用id获取
	var content = $("#message-text").val();

	//发异步请求(post)
	$.post(
		//使用JQuery发送请求
		CONTEXT_PATH + "/discuss/add",//1.访问路径
		{"title":title,"content":content},//2.需要向服务器提交的数据，声明一个json对象
		function (data){//3.声明一个匿名的回调函数,处理服务器返回的结果
			//返回的结果是一个字符串，需要改成js对象
			data = $.parseJSON(data);
			//在提示框中显示返回的消息，将提示消息显示到提示框中
			$("#hintBody").text(data.msg)//通过$("#hintBody")获取提示框，通过.text修改内容；这里的msg是CommunityUtil中的key

			//显示提示框
			$("#hintModal").modal("show");
			//2秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//如果添加成功刷新页面，能够看到刚才添加的数据
				if (data.code == 0){
					window.location.reload();
				}
				//如果添加失败报错就不刷新
			}, 2000);
		}

	);


}