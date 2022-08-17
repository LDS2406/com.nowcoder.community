function like(btn, entityType, entityId, entityUserId,postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},//向服务器提交的数据
        function (data) {//服务器返回给浏览器的数据
            data = $.parseJSON(data);
            if (data.code == 0){
                //通过按钮得到子标签
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? "已赞":"赞");
            }else {
                alert(data.msg);
            }
        }
    );
}