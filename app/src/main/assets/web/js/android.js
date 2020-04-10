;(function(win){
	"use strict"
	var _global;
	//构造方法 初始化 setting
	function extend(o,n,override) {
	    for(var key in n){
	      if(n.hasOwnProperty(key) && (!o.hasOwnProperty(key) || override)){
	        o[key]=n[key];
	      }
	    }
	  return o;
	};
	
	// 插件构造函数 - 返回数组结构
	function AndroidJsBridge(option){
	  this._initial(option);
	};
	AndroidJsBridge.prototype={
		constructor:this,
		_initial:function(option){
			this.version = '1.0.0',
			this.name = 'Android插件',
			this.date = new Date("2020-03-18 11:04".replace(/-/g,"/")),
			this.type = {
				'string':'js://string',
				'json':'js://json',
			}
			/* var def = {
				
			}
			this.def = extend(this,option,true); //配置参数 */
		},
		isArray(o){
		    return Object.prototype.toString.call(o)== '[object Array]';
		},
		show:function(msg){
			alert(msg)
		},
		sendText:function(msg){
			var result = prompt(`${this.type.string}?${msg}`);
			return result;
		},
		sendJson:function(obj){
			var msg="",msgArr = this.wrapJson(obj);
			if(this.isArray(msgArr)){
				msg = msgArr.join("&");
			}
			// 调用prompt()> Android端接收和处理 > JsPromptResult.confirm()返回结果给web端
			var result = prompt(`${this.type.json}?${msg}`);
			return result;
		},
		wrapJson:function(obj){
			var temp =[];
			for(var i in obj){
				temp.push(i+"="+obj[i])
			}
			return temp;
		}
	}
	// 将插件对象暴露给全局对象（考虑兼容性）
	  _global = (function(){ return this || (0, eval)('this'); }());
	  if (typeof module !== "undefined" && module.exports) {
	    module.exports = AndroidJsBridge;
	  } else if (typeof define === "function" && define.amd) {
	    define(function(){return AndroidJsBridge;});
	  } else {
	    !('AndroidJsBridge' in _global) && (_global.AndroidJsBridge = AndroidJsBridge);
	  }
})()