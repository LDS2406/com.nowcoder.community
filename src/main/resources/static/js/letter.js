$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");//隐藏弹出框

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	//发异步请求
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			if (data.code == 0){
				$("#hintBody").text("发送成功");
			}else {
				$("#hintBody").text(data.msg);
			}
			$("#hintModal").modal("show");//显示提示框
			setTimeout(function(){//2秒后关闭提示框
				$("#hintModal").modal("hide");
				location.reload();//刷新当前页面
			}, 2000);
		}
	);


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}