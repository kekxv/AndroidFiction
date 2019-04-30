# AndroidFiction

仅供学习参考



## 接口说明

### window.NavigatorCallback

回调接口

用于与 `Android` 异步交互作用，避免非全局函数无法回调问题。

#### pushCallback

参数 ： `回调函数`，`上下文`。

返回 ： `回调ID`

#### runCallback

执行回调函数

参数 ： `回调ID` , `不定参数`

返回 ： 执行结果

### NavigatorAPI

多线程异步请求接口API。

#### GetData

参数 ： `地址`，`成功回调ID`，`失败回调ID`

返回 ： 异步无返回

回调参数：

`JSON` 格式

> 返回说明
>
> 1. `Code` 结果代码
> 2. `Message` 结果信息
> 3. `Result:` 结果内容

#### PutData

参数 ： `地址`，`POST 参数` `[JSON] 字符串格式`，`成功回调ID`，`失败回调ID`

返回 ： 异步无返回

回调参数：

`JSON` 格式

> 返回说明
>
> 1. `Code` 结果代码
> 2. `Message` 结果信息
> 3. `Result:` 结果内容


#### PutData

参数 ： `地址`，`POST 参数` `[JSON] 字符串格式`，`成功回调ID`，`失败回调ID`

返回 ： 异步无返回

回调参数：

`JSON` 格式

> 返回说明
>
> 1. `Code` 结果代码
> 2. `Message` 结果信息
> 3. `Result:` 结果内容



## 举个🌰

学习项目 ： [Fiction](https://github.com/kekxv/Fiction) 

```javascript
GetData: function (url, callback, err) {
        if (API.IsClient()) {
            NavigatorAPI.GetData(
                url,
                // 获取回调 ID
                NavigatorCallback.pushCallback(function (...values) {
                    callback && callback(values[0]);
                }),
                // 获取回调 ID
                NavigatorCallback.pushCallback(function (...values) {
                    err && err(values[0]);
                })
            );
            return;
        }
        let headers = {};
        fetch("{{0}}?url={{1}}".format(ProxyCrossDomainUrl, encodeURIComponent(url)),
            {
                cache: 'no-cache', 
                // credentials: 'include', // include, same-origin, *omit
                method: 'GET', // *GET, POST, PUT, DELETE, etc.
                headers: headers,
                mode: 'cors', // no-cors, cors, *same-origin
                redirect: 'follow', // manual, *follow, error
                referrer: 'no-referrer', // *client, no-referrer
            }
        )
            .then(function (response) {
                return response.json();
            }).then(callback || console.log).catch(err || console.log).catch(err || console.log)
    }
```

