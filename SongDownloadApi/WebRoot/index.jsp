<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    	<script type="text/javascript">
    	
    	get_url: function(type, data){
            var urls = {
                    base: 'http://tingapi.ting.baidu.com/v1/restserver/ting', 
                    sug: 'http://sug.music.baidu.com/info/suggestion', 
                    album: 'http://music.baidu.com/data/music/box/album', 
                    download: 'http://music.baidu.com/data/music/fmlink'};
            var param = [];
            for (var key in data) {
                param.push( key +'='+ data[key] );
            }; 
            return  urls[type] + '?' + param.join('&');
            //http://music.baidu.com/data/music/fmlink?songIds=124169979&rate=320&type=flac,mp3
        },
        
        case 'download':
                url = me.get_url(action, param);
                var __key = param.songIds + '_' + (param.type=='flac,mp3' ? 'flac' : 'mp3');
                if ( me.data.download[__key] ) { 
                    return me.cb_download( me.data.download[__key], true ); 
                }
                break;


        bind_download: function(){
            var me = this;
            $('#data_list .act_btn_download').on('click', function(){
                var _param = me.param( $(this).attr('href') );//download/124169979/flac
                var _songid = _param[1];
                var _rate = _param[2];
                var _type = '';
                if ( _rate == 'flac' ) { 
                    _type = 'flac,mp3';
                    _rate = '320'
                }
                me.get_data('download', {songIds: 124169979, rate: '320', type: 'flac,mp3'});
            });
        },


    	</script>
  </body>
</html>
