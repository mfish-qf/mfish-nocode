let app = new Vue({
    el: '#newLogin',
    data: {
        userPasswordVisible: false,
        phoneSmsCodeVisible: false,
        qrCodeVisible: false,
        captchaValue: '',
        captchaKey: '',
        captchaUrl: '',
        phone: '',
        username: 'admin',
        password: '!QAZ2wsx',
        loginType: '',
        loginTypeName: '帐号',
        errorVisible: false,
        errorMsg: '',
        rememberMe: false,
        codeValue: '',
        codeButton: '',
        codeDisabled: false,
        qrCode: '',
        timer: '',
        qrCodeDesc: '打开小程序扫码',
        qrCodeName: '',
        qrCodeSecret: '',
        allowScan: true,
        showLeft: true,
        error: {
            username: {
                show: false,
                msg: ''
            },
            password: {
                show: false,
                msg: ''
            },
            captcha: {
                show: false,
                msg: ''
            },
            phone: {
                show: false,
                msg: ''
            },
            code: {
                show: false,
                msg: ''
            }
        }
    },
    mounted() {
        this.initLoginData();
        this.getCaptcha();
        this.showError();
        this.screenChange();
        window.onresize = () => {
            return this.screenChange()
        }
    },
    methods: {
        screenChange() {
            const clientWidth = document.body.clientWidth
            const clientHeight = document.body.clientHeight
            if (clientWidth > 425) {
                this.showLeft = true;
            } else {
                this.showLeft = false;
            }
            return clientWidth;
        },
        login() {
            if (this.validateUserLogin()) {
                $('#login').submit();
            }
        },
        validateUserLogin() {
            if (!this.username) {
                this.showInputError('username', '请输入用户名')
                return false
            }
            if (!this.password) {
                this.showInputError('password', '请输入密码')
                return false
            }
            if (!this.captchaValue) {
                this.showInputError('captcha', '请输入验证码')
                return false
            }
            return true
        },
        showInputError(key, error) {
            this.setError(key, true, error);
        },
        hideInputError(key) {
            this.setError(key, false, '')
        },
        setError(key, show, error) {
            this.error[key].show = show;
            this.error[key].msg = error;
        },
        smsLogin() {
            if (this.validateSmsLogin()) {
                $('#smsLogin').submit();
            }
        },
        validateSmsLogin() {
            if (!this.validatePhone()) {
                return false
            }
            if (!this.codeValue) {
                this.showInputError('code', '请输入验证码')
                return false
            }
            return true;
        },
        validatePhone() {
            if (!this.phone) {
                this.showInputError('phone', '请输入手机号')
                return false
            }
            if (!new RegExp(/^1[3-9][0-9]\d{8}$/).test(this.phone)) {
                this.showInputError('phone', '手机号格式不正确')
                return false
            }
            return true
        },

        sendMsg() {
            if (this.validatePhone()) {
                $.ajax({
                    url: "sendMsg",
                    type: "POST",
                    data: "phone=" + this.phone,
                    dataType: "json",
                    success: function (result) {
                        if (200 == result.code) {
                            //测试环境将验证码返回，生成环境删除此方法
                            app.codeValue = result.data;
                            app.resetCode();
                        } else {
                            app.showError(result.msg);
                        }
                    }
                });
            }
        },

        getCaptcha() {
            $.ajax({
                url: "/captcha",
                type: "get",
                success: function (result) {
                    if (200 == result.code) {
                        app.captchaUrl = "data:image/gif;base64," + result.data.img;
                        app.captchaKey = result.data.captchaKey;
                    } else {
                        app.showError(result.msg);
                    }
                }
            });
        },
        buildQRCode() {
            $.ajax({
                url: "qrCodeLogin/build",
                type: "get",
                success: function (result) {
                    if (result != null && 200 == result.code) {
                        app.qrCode = result.data.img;
                        app.allowScan = true;
                        app.qrCodeDesc = "打开小程序扫码";
                        app.checkQRCode(300, result.data.code);
                    }
                }
            });
        },
        checkQRCode(second, code) {
            clearInterval(app.timer);
            app.timer = setInterval(function () {
                second -= 2;
                if (second > 0) {
                    app.waitPhoneScan(app.timer, code);
                } else {
                    app.InvalidQRCode(app.timer);
                }
            }, 2000);
        },
        waitPhoneScan(timer, code) {
            $.ajax({
                url: "qrCodeLogin/check?code=" + code,
                dataType: "json",
                type: "get",
                success: function (result) {
                    if (result == null || result.data.status == null) {
                        app.InvalidQRCode(timer);
                        return;
                    }
                    switch (result.data.status) {
                        case '1':
                            app.qrCode = "img/qrcode_ok.png";
                            app.qrCodeDesc = "手机确认登录";
                            app.qrCodeName = result.data.account;
                            break;
                        case '2':
                            clearInterval(timer);
                            app.qrCodeSecret = code + "," + result.data.secret;
                            break;
                        case '3':
                            app.InvalidQRCode(timer);
                            break;
                        default:
                            break;
                    }
                }
            });
        },
        InvalidQRCode(timer) {
            app.qrCodeDesc = "点击图片刷新";
            app.allowScan = false;
            clearInterval(timer);
        },
        showUserPassword() {
            this.userPasswordVisible = true;
            this.phoneSmsCodeVisible = false;
            this.qrCodeVisible = false;
            this.loginType = "user_password";
            this.loginTypeName = "帐号";
            clearInterval(this.timer)
            this.clearError();
        },
        showPhoneSmsCode() {
            this.phoneSmsCodeVisible = true;
            this.userPasswordVisible = false;
            this.qrCodeVisible = false;
            this.loginType = "phone_smsCode";
            this.loginTypeName = "手机";
            clearInterval(this.timer)
            this.clearError();
        },
        showQrCode() {
            this.qrCodeVisible = true;
            this.phoneSmsCodeVisible = false;
            this.userPasswordVisible = false;
            this.loginType = "qr_code";
            this.loginTypeName = "扫码";
            this.buildQRCode();
            this.clearError();
        },
        refreshQrCode() {
            this.buildQRCode();
        },
        clearError() {
            this.errorMsg = '';
            this.errorVisible = false;
        },
        showError(error) {
            if (error != null && error != '') {
                this.errorMsg = error;
            } else {
                this.errorMsg = $('#errorMsg').val();
            }
            if (this.errorMsg !== '' && this.errorMsg !== undefined) {
                this.errorVisible = true;
            }
        },
        initLoginData() {
            this.loginType = $('#loginType').val();
            switch (this.loginType) {
                case "phone_smsCode":
                    this.phone = $('#username').val();
                    this.showPhoneSmsCode();
                    break;
                case "qr_code":
                    this.showQrCode();
                    break;
                default:
                    this.username = $('#username').val();
                    this.showUserPassword();
                    break;
            }
        },
        resetCode() {
            let second = 60;
            let codeTime = setInterval(function () {
                second -= 1;
                if (second > 0) {
                    app.codeButton = second;
                    app.codeDisabled = true;
                } else {
                    clearInterval(codeTime);
                    app.codeButton = "";
                    app.codeDisabled = false;
                }
            }, 1000);
        }
    },
});
app.$watch('qrCodeSecret', function () {
    $('#qrCodeLogin').submit();
});
