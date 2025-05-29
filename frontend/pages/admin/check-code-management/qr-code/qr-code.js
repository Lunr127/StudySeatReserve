const app = getApp();
const { checkCodeApi } = require('../../../../utils/api');
const { drawQRCodeOnCanvas } = require('../../../../utils/qrcode');

Page({
  data: {
    checkCode: null, // 签到码信息
    qrCodeUrl: '', // 二维码图片URL
    loading: true,
    canvasWidth: 300,
    canvasHeight: 300,
    errorMsg: '' // 错误信息
  },

  onLoad: function(options) {
    const checkCodeStr = options.checkCode;
    if (checkCodeStr) {
      try {
        const checkCode = JSON.parse(decodeURIComponent(checkCodeStr));
        this.setData({
          checkCode: checkCode
        });
        // 延迟一下再生成二维码，确保Canvas已经渲染
        setTimeout(() => {
          this.generateQRCodeLegacy(); // 直接使用旧版API方法更稳定
        }, 500);
      } catch (e) {
        console.error('解析签到码数据失败:', e);
        wx.showToast({
          title: '数据错误',
          icon: 'error'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    } else {
      wx.showToast({
        title: '缺少必要参数',
        icon: 'error'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  /**
   * 生成二维码
   */
  generateQRCode: function() {
    const { checkCode } = this.data;
    if (!checkCode) return;
    
    wx.showLoading({
      title: '生成二维码中...'
    });

    // 构造二维码内容，包含签到码和自习室信息
    const qrData = {
      code: checkCode.code,
      studyRoomId: checkCode.studyRoomId,
      studyRoomName: checkCode.studyRoomName,
      validDate: checkCode.validDate,
      type: 'check_in_code'
    };

    const qrContent = JSON.stringify(qrData);
    
    try {
      // 使用新的方式获取Canvas
      const query = wx.createSelectorQuery();
      query.select('#qrCodeCanvas')
        .fields({ node: true, size: true })
        .exec((res) => {
          if (!res || !res[0] || !res[0].node) {
            this.handleQRCodeError('Canvas节点获取失败');
            // 失败后尝试使用旧版API
            setTimeout(() => {
              this.generateQRCodeLegacy();
            }, 300);
            return;
          }

          const canvas = res[0].node;
          // 设置Canvas实际尺寸
          canvas.width = this.data.canvasWidth;
          canvas.height = this.data.canvasHeight;
          
          const ctx = canvas.getContext('2d');
          
          // 绘制二维码
          try {
            drawQRCodeOnCanvas(ctx, qrContent, this.data.canvasWidth, 25);
            
            // 获取临时图片路径
            wx.canvasToTempFilePath({
              canvas: canvas,
              success: (res) => {
                console.log('生成二维码图片成功:', res.tempFilePath);
                this.setData({
                  qrCodeUrl: res.tempFilePath,
                  loading: false
                });
                wx.hideLoading();
              },
              fail: (err) => {
                this.handleQRCodeError('生成二维码图片失败: ' + err.errMsg);
                // 失败后尝试使用旧版API
                setTimeout(() => {
                  this.generateQRCodeLegacy();
                }, 300);
              }
            });
          } catch (error) {
            this.handleQRCodeError('绘制二维码失败: ' + error.message);
            // 失败后尝试使用旧版API
            setTimeout(() => {
              this.generateQRCodeLegacy();
            }, 300);
          }
        });
    } catch (error) {
      this.handleQRCodeError('二维码生成异常: ' + error.message);
      // 失败后尝试使用旧版API
      setTimeout(() => {
        this.generateQRCodeLegacy();
      }, 300);
    }
  },

  /**
   * 处理二维码生成错误
   */
  handleQRCodeError: function(errorMsg) {
    console.error(errorMsg);
    wx.hideLoading();
    this.setData({
      errorMsg: errorMsg
    });
  },

  /**
   * 使用旧版Canvas API生成二维码(备用方法)
   */
  generateQRCodeLegacy: function() {
    const { checkCode } = this.data;
    if (!checkCode) return;
    
    wx.showLoading({
      title: '生成二维码中...'
    });
    
    // 清除可能存在的错误信息
    this.setData({
      errorMsg: ''
    });
    
    const ctx = wx.createCanvasContext('qrCodeCanvas', this);
    const { canvasWidth, canvasHeight } = this.data;
    
    // 清空画布
    ctx.setFillStyle('#ffffff');
    ctx.fillRect(0, 0, canvasWidth, canvasHeight);
    
    try {
      // 生成二维码内容
      const qrContent = JSON.stringify({
        code: checkCode.code,
        studyRoomId: checkCode.studyRoomId,
        studyRoomName: checkCode.studyRoomName,
        validDate: checkCode.validDate,
        type: 'check_in_code'
      });
      
      // 使用专业的QR码生成器
      drawQRCodeOnCanvas(ctx, qrContent, canvasWidth, 25);
      
      ctx.draw(true, () => {
        // 等待300ms确保绘制完成
        setTimeout(() => {
          // 生成图片
          wx.canvasToTempFilePath({
            canvasId: 'qrCodeCanvas',
            success: (res) => {
              console.log('旧版API生成二维码成功:', res.tempFilePath);
              this.setData({
                qrCodeUrl: res.tempFilePath,
                loading: false
              });
              wx.hideLoading();
            },
            fail: (err) => {
              console.error('旧版API生成图片失败:', err);
              wx.hideLoading();
              wx.showToast({
                title: '生成二维码失败',
                icon: 'error'
              });
              this.setData({
                loading: false,
                errorMsg: '生成图片失败，请重试'
              });
            }
          }, this);
        }, 500);
      });
    } catch (error) {
      console.error('旧版API绘制失败:', error);
      wx.hideLoading();
      wx.showToast({
        title: '生成二维码失败',
        icon: 'error'
      });
      this.setData({
        loading: false,
        errorMsg: '绘制失败，请重试'
      });
    }
  },

  /**
   * 重试生成二维码
   */
  retryGenerateQRCode: function() {
    this.setData({ loading: true, errorMsg: '' });
    wx.showToast({
      title: '正在重试...',
      icon: 'loading',
      duration: 1000
    });
    
    setTimeout(() => {
      // 直接使用旧版API方法更稳定
      this.generateQRCodeLegacy();
    }, 1000);
  },

  /**
   * 复制签到码
   */
  copyCheckCode: function() {
    const { checkCode } = this.data;
    if (!checkCode) return;
    
    wx.setClipboardData({
      data: checkCode.code,
      success: function() {
        wx.showToast({
          title: '签到码已复制',
          icon: 'success'
        });
      }
    });
  },

  /**
   * 保存二维码到相册
   */
  saveQRCode: function() {
    const { qrCodeUrl } = this.data;
    if (!qrCodeUrl) {
      wx.showToast({
        title: '二维码还未生成',
        icon: 'none'
      });
      return;
    }
    
    wx.saveImageToPhotosAlbum({
      filePath: qrCodeUrl,
      success: function() {
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        });
      },
      fail: function(err) {
        if (err.errMsg.includes('auth')) {
          wx.showModal({
            title: '授权提示',
            content: '需要授权保存图片到相册',
            success: function(res) {
              if (res.confirm) {
                wx.openSetting();
              }
            }
          });
        } else {
          wx.showToast({
            title: '保存失败',
            icon: 'error'
          });
        }
      }
    });
  },

  /**
   * 分享二维码
   */
  shareQRCode: function() {
    const { checkCode } = this.data;
    if (!checkCode) return;
    
    // 注意：实际分享功能需要根据需求实现
    wx.showToast({
      title: '分享功能开发中',
      icon: 'none'
    });
  },

  /**
   * 返回上一页
   */
  goBack: function() {
    wx.navigateBack();
  },

  /**
   * 格式化日期
   */
  formatDate: function(date) {
    if (!date) return '';
    return date.replace(/-/g, '/');
  }
}); 