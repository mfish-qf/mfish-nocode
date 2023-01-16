const scanStatus = {
    未扫描: 0,
    已扫描: 1,
    已确认: 2,
    已取消: 3
}
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
        scanStatus: scanStatus,
        showLeft: true,
        isValid: false,
        sendMsgColor: 'black',
        passwordShow: false,
        passwordType: 'password',
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
            if (clientWidth > 425) {
                this.showLeft = true;
            } else {
                this.showLeft = false;
            }
            return clientWidth;
        },
        login() {
            if (this.validateUserLogin()) {
                this.isValid = true
                //增加一个停顿校验效果
                setTimeout(() => {
                    $('#login').submit();
                }, 100)
            }
        },
        validateUserLogin() {
            if (!this.validateUserName() || !this.validatePassword() || !this.validateCaptcha()) {
                return false;
            }
            return true
        },
        validateUserName() {
            if (!this.username) {
                this.showInputError('username', '请输入用户名')
                return false
            }
            this.hideInputError('username')
            return true
        },
        validatePassword() {
            if (!this.password) {
                this.showInputError('password', '请输入密码')
                return false
            }
            this.hideInputError('password')
            return true;
        },
        validateCaptcha() {
            if (!this.captchaValue) {
                this.showInputError('captcha', '请输入验证码')
                return false
            }
            this.hideInputError('captcha')
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
                this.isValid = true
                //增加一个停顿校验效果
                setTimeout(() => {
                    $('#smsLogin').submit();
                }, 100)
            }
        },
        validateSmsLogin() {
            if (!this.validatePhone() || !this.validateCode()) {
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
            this.hideInputError('phone')
            return true
        },
        validateCode() {
            if (!this.codeValue) {
                this.showInputError('code', '请输入验证码')
                return false
            }
            this.hideInputError('code')
            return true
        },
        sendMsg() {
            if (this.codeDisabled) {
                return;
            }
            if (this.validatePhone()) {
                this.codeDisabled = true;
                $.ajax({
                    url: "sendMsg",
                    type: "POST",
                    data: "phone=" + this.phone,
                    dataType: "json",
                    success: function (result) {
                        let time = 60;
                        if (200 == result.code) {
                            //测试环境将验证码返回，生成环境删除此方法
                            app.codeValue = result.data;
                        } else {
                            time = result.data
                            app.showError(result.msg);
                        }
                        app.codeButton = time;
                        app.resetCode(time);
                    },
                    error: () => {
                        this.codeDisabled = false
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
                        app.scanStatus = scanStatus.未扫描
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
                    if (result?.data?.status == null) {
                        app.InvalidQRCode(timer);
                        return;
                    }
                    switch (result.data.status) {
                        case '1':
                            app.scanStatus = scanStatus.已扫描
                            app.qrCodeDesc = "手机确认登录";
                            app.qrCodeName = result.data.account;
                            break;
                        case '2':
                            clearInterval(timer);
                            app.scanStatus = scanStatus.已确认
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
            app.scanStatus = scanStatus.已取消
            app.qrCodeDesc = "点击图片刷新";
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
                $('#errorShow').click()
                //两秒后关闭
                setTimeout(() => {
                    if ($('#errorModal').attr('aria-modal')) {
                        $('#errorShow').click()
                    }
                }, 2000)
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
        resetCode(time) {
            let codeTime = setInterval(() => {
                time -= 1;
                if (time > 0) {
                    app.codeButton = time;
                    app.codeDisabled = true;
                } else {
                    clearInterval(codeTime);
                    app.codeButton = "";
                    app.codeDisabled = false;
                }
            }, 1000);
        },
        msgOver() {
            this.sendMsgColor = '#0d6efd';
        },
        msgLeave() {
            this.sendMsgColor = 'black';
        },
        pwdShowChange() {
            this.passwordShow = !this.passwordShow;
            if (this.passwordShow) {
                this.passwordType = 'text'
            } else {
                this.passwordType = 'password'
            }
        }
    },
});
app.$watch('qrCodeSecret', function () {
    $('#qrCodeLogin').submit();
});
