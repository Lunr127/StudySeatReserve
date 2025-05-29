const app = getApp();
const { checkCodeApi } = require('../../../../utils/api');
const { drawQRCodeOnCanvas } = require('../../../../utils/qrcode');

Page({
  data: {
    checkCode: null, // 签到码信息
    qrCodeUrl: '', // 二维码图片URL
    loading: true,
    canvasWidth: 500,
    canvasHeight: 500
  },

  onLoad: function(options) {
    const checkCodeStr = options.checkCode;
    if (checkCodeStr) {
      try {
        const checkCode = JSON.parse(decodeURIComponent(checkCodeStr));
        this.setData({
          checkCode: checkCode
        });
        this.generateQRCode();
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
    
    // 使用Canvas生成二维码
    this.drawQRCode(qrContent);
  },

  /**
   * 使用Canvas绘制二维码
   */
  drawQRCode: function(content) {
    const ctx = wx.createCanvasContext('qrCodeCanvas', this);
    const { canvasWidth, canvasHeight } = this.data;
    
    try {
      // 使用专业的QR码生成器
      drawQRCodeOnCanvas(ctx, content, canvasWidth, 25);
      
      ctx.draw(true, () => {
        // 生成图片
        wx.canvasToTempFilePath({
          canvasId: 'qrCodeCanvas',
          success: (res) => {
            this.setData({
              qrCodeUrl: res.tempFilePath,
              loading: false
            });
            wx.hideLoading();
          },
          fail: (err) => {
            console.error('生成二维码图片失败:', err);
            wx.hideLoading();
            wx.showToast({
              title: '生成二维码失败',
              icon: 'error'
            });
            this.setData({
              loading: false
            });
          }
        }, this);
      });
    } catch (error) {
      console.error('绘制二维码失败:', error);
      wx.hideLoading();
      wx.showToast({
        title: '生成二维码失败',
        icon: 'error'
      });
      this.setData({
        loading: false
      });
    }
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