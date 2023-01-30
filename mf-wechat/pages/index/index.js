//index.js
//获取应用实例
const app = getApp()

Page({
    data: {
        userInfo: {},
        hasUserInfo: false,
        canIUse: wx.canIUse('button.open-type.getUserInfo'),
        isBind: false,
        username: '',
        password: '',
        nickname: '',
        phone: ''
    },
    //事件处理函数
    bindViewTap: function () {
        wx.navigateTo({
            url: '../logs/logs'
        })
    },
    onLoad: function () {
        if (app.globalData.userInfo) {
            this.setData({
                userInfo: app.globalData.userInfo,
                hasUserInfo: true
            })
        } else if (this.data.canIUse) {
            // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
            // 所以此处加入 callback 以防止这种情况
            app.userInfoReadyCallback = res => {
                this.setData({
                    userInfo: res.userInfo,
                    hasUserInfo: true
                })
            }
        } else {
            // 在没有 open-type=getUserInfo 版本的兼容处理
            wx.getUserInfo({
                success: res => {
                    app.globalData.userInfo = res.userInfo
                    this.setData({
                        userInfo: res.userInfo,
                        hasUserInfo: true
                    })
                }
            })
        }
        this.checkUser();
    },
    checkUser: function () {
        let _this = this;
        // 登录
        wx.login({
            success: res => {
                wx.request({
                    url: app.globalData.domain + '/wx/bind/check',
                    data: {
                        code: res.code
                    },
                    header: {
                        'content-type': 'application/json'
                    },
                    success(res) {
                        if (res.statusCode == 200) {
                            let token = res.data;
                            if (token == null || token == '') {
                                _this.setData({
                                    isBind: false
                                })
                            } else {
                                wx.setStorageSync("access_token", token);
                                _this.getUser();
                                _this.setData({
                                    isBind: true
                                })
                            }
                        }
                    }
                })
            }
        })
    },
    onShow: function () {

    },
    getUserInfo: function (e) {
        app.globalData.userInfo = e.detail.userInfo
        this.setData({
            userInfo: e.detail.userInfo,
            hasUserInfo: true
        })
    },
    bindWeChat() {
        let _this = this;
        wx.login({
            success: res => {
                wx.request({
                    url: app.globalData.domain + '/wx/bind',
                    method: 'POST',
                    data: {
                        code: res.code,
                        username: this.data.username,
                        password: this.data.password
                    },
                    header: {
                        'content-type': 'application/x-www-form-urlencoded'
                    },
                    success(res) {
                        if (res.statusCode == 200) {
                            wx.setStorageSync("access_token", res.data);
                            _this.isBind = true;
                            _this.getUser();
                        }

                    }
                })
            }
        })
    },
    getUser() {
        let _this = this;
        wx.request({
            url: app.globalData.domain + '/user/info',
            data: {
                access_token: wx.getStorageSync("access_token").access_token,
            },
            header: {
                'content-type': 'application/x-www-form-urlencoded'
            },
            success(res) {
                if (res.statusCode == 200) {
                    _this.setData({
                        username: res.data.data.account,
                        nickname: res.data.data.nickname,
                        phone: res.data.data.phone
                    })
                }
            }
        })
    },
    getScancode() {
        let _this = this;
        wx.scanCode({
            success: (res) => {
                let result = res.result;
                _this.scanQrCode(result);
            }
        })
    },
    scanQrCode(code) {
        let _this = this;
        wx.request({
            url: app.globalData.domain + '/qrCodeLogin/scan',
            data: {
                access_token: wx.getStorageSync("access_token").access_token,
                code: code
            },
            method: 'POST',
            header: {
                'content-type': 'application/x-www-form-urlencoded'
            },
            success(result) {
                if (result.statusCode == 200) {
                    console.log(result);
                    if (result.data.success) {
                        wx.showModal({
                            title: '登录提示',
                            content: '登录成功',
                            confirmText: "确认登录",
                            cancelText: "取消登录",
                            success: function (res) {
                                if (res.confirm) {
                                    _this.qrCodeLogin(code, result.data.data);
                                } else {
                                    _this.qrCodeCancel(code, result.data.data)
                                }
                            }
                        });
                    } else {
                        wx.showToast({
                            title: result.data.msg,
                            icon: 'none',
                            duration: 2000
                        })
                    }
                } else {
                    wx.showToast({
                        title: result.errMsg,
                        icon: 'none',
                        duration: 2000
                    })
                }
            }
        })
    },
    qrCodeLogin(code, secret) {
        let _this = this;
        wx.request({
            url: app.globalData.domain + '/qrCodeLogin/login',
            data: {
                access_token: wx.getStorageSync("access_token").access_token,
                code: code,
                qrSecret: secret
            },
            method: 'POST',
            header: {
                'content-type': 'application/x-www-form-urlencoded'
            },
            success(res) {
                if (res.statusCode == 200) {
                    if (res.data.success) {
                        wx.showToast({
                            title: "登录成功",
                            icon: 'success',
                            duration: 2000
                        })
                    } else {
                        wx.showToast({
                            title: res.data.msg,
                            icon: 'none',
                            duration: 2000
                        })
                    }
                } else {
                    wx.showToast({
                        title: res.errMsg,
                        icon: 'none',
                        duration: 2000
                    })
                }
            }
        })
    },
    qrCodeCancel(code, secret) {
        let _this = this;
        wx.request({
            url: app.globalData.domain + '/qrCodeLogin/cancel',
            data: {
                access_token: wx.getStorageSync("access_token").access_token,
                code: code,
                qrSecret: secret
            },
            method: 'POST',
            header: {
                'content-type': 'application/x-www-form-urlencoded'
            },
            success(res) {
                if (res.statusCode == 200) {
                    if (res.data.success) {
                        wx.showToast({
                            title: "取消登录成功",
                            icon: 'success',
                            duration: 2000
                        })
                    } else {
                        wx.showToast({
                            title: res.data.msg,
                            icon: 'none',
                            duration: 2000
                        })
                    }
                } else {
                    wx.showToast({
                        title: res.errMsg,
                        icon: 'none',
                        duration: 2000
                    })
                }
            }
        })
    },
    inputUserName: function (e) {
        this.setData({
            username: e.detail.value
        })
    },
    inputPassword: function (e) {
        this.setData({
            password: e.detail.value
        })
    }
})