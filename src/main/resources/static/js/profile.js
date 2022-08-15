$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;//获取当前按钮
	if($(btn).hasClass("btn-info")) {//判断按钮的样式是蓝色的，表示可以关注
		// 关注TA
		$.post(
			CONTEXT_PATH + "/follow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if (data.code == 0){
					window.location.reload();//直接刷新页面
				}else {
					alert(data.msg);
				}
			}
		);
		//$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {//判断按钮的样式是灰色的，表示可以取消关注
		// 取消关注
		$.post(
			CONTEXT_PATH + "/unfollow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if (data.code == 0){
					window.location.reload();//直接刷新页面
				}else {
					alert(data.msg);
				}
			}
		);
		//$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}