const { checkInApi, checkCodeApi, reservationApi } = require('../../utils/api');

Page({
  data: {
    // 页面状态
    loading: false,
    
    // 预约信息
    reservation: null,
    
    // 签到方式：1-扫码签到，2-手动输入编码
    checkInType: 1,
    
    // 手动输入的签到码
    inputCode: '',
    
    // 当前自习室的签到码信息
    todayCheckCode: null,
    
    // 签到状态
    checkInStatus: null, // null-未签到，1-已签到未签退，2-已签退
    
    // 签到信息
    checkInInfo: null
  },

  onLoad(options) {
    const { reservationId } = options;
    if (reservationId) {
      this.setData({ reservationId });
      this.loadReservationInfo(reservationId);
      this.checkExistingCheckIn(reservationId);
    } else {
      wx.showToast({
        title: '预约信息不存在',
        icon: 'error'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }
  },

  /**
   * 加载预约信息
   */
  async loadReservationInfo(reservationId) {
    try {
      this.setData({ loading: true });
      
      const response = await reservationApi.getReservationDetail(reservationId);
      if (response.success && response.data) {
        this.setData({ reservation: response.data });
        
        // 加载当日签到码信息
        await this.loadTodayCheckCode(response.data.studyRoomId);
      } else {
        throw new Error(response.message || '获取预约信息失败');
      }
    } catch (error) {
      console.error('加载预约信息失败:', error);
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'error'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  /**
   * 加载当日签到码信息
   */
  async loadTodayCheckCode(studyRoomId) {
    try {
      const response = await checkCodeApi.getTodayCheckCode(studyRoomId);
      if (response.success && response.data) {
        this.setData({ todayCheckCode: response.data });
      }
    } catch (error) {
      console.error('加载签到码失败:', error);
    }
  },

  /**
   * 检查是否已经签到
   */
  async checkExistingCheckIn(reservationId) {
    try {
      const response = await checkInApi.getCheckInByReservation(reservationId);
      if (response.success && response.data) {
        const checkInInfo = response.data;
        this.setData({ 
          checkInInfo,
          checkInStatus: checkInInfo.isCheckedOut ? 2 : 1
        });
      }
    } catch (error) {
      console.error('检查签到状态失败:', error);
    }
  },

  /**
   * 切换签到方式
   */
  onCheckInTypeChange(e) {
    const checkInType = parseInt(e.currentTarget.dataset.type);
    this.setData({ checkInType });
  },

  /**
   * 输入签到码
   */
  onInputCodeChange(e) {
    this.setData({ inputCode: e.detail.value });
  },

  /**
   * 扫码签到
   */
  onScanCode() {
    wx.scanCode({
      success: (res) => {
        // 这里可以解析二维码内容，获取签到码
        // 假设二维码内容就是签到码
        const code = res.result;
        this.performCheckIn(1, code);
      },
      fail: (error) => {
        console.error('扫码失败:', error);
        wx.showToast({
          title: '扫码失败',
          icon: 'error'
        });
      }
    });
  },

  /**
   * 手动输入签到
   */
  onManualCheckIn() {
    const { inputCode } = this.data;
    if (!inputCode || inputCode.trim() === '') {
      wx.showToast({
        title: '请输入签到码',
        icon: 'error'
      });
      return;
    }
    
    this.performCheckIn(2, inputCode.trim());
  },

  /**
   * 执行签到
   */
  async performCheckIn(checkInType, checkCode) {
    const { reservation } = this.data;
    if (!reservation) {
      wx.showToast({
        title: '预约信息不存在',
        icon: 'error'
      });
      return;
    }

    try {
      this.setData({ loading: true });

      const checkInData = {
        reservationId: reservation.id,
        checkInType: checkInType,
        checkCode: checkCode,
        studyRoomId: reservation.studyRoomId
      };

      const response = await checkInApi.checkIn(checkInData);
      if (response.success) {
        wx.showToast({
          title: '签到成功',
          icon: 'success'
        });
        
        // 重新检查签到状态
        await this.checkExistingCheckIn(reservation.id);
      } else {
        throw new Error(response.message || '签到失败');
      }
    } catch (error) {
      console.error('签到失败:', error);
      wx.showToast({
        title: error.message || '签到失败',
        icon: 'error'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  /**
   * 签退
   */
  async onCheckOut() {
    const { reservation } = this.data;
    if (!reservation) {
      wx.showToast({
        title: '预约信息不存在',
        icon: 'error'
      });
      return;
    }

    wx.showModal({
      title: '确认签退',
      content: '确定要签退吗？签退后将结束本次自习。',
      success: async (res) => {
        if (res.confirm) {
          try {
            this.setData({ loading: true });
            
            const response = await checkInApi.checkOut(reservation.id);
            if (response.success) {
              wx.showToast({
                title: '签退成功',
                icon: 'success'
              });
              
              // 重新检查签到状态
              await this.checkExistingCheckIn(reservation.id);
            } else {
              throw new Error(response.message || '签退失败');
            }
          } catch (error) {
            console.error('签退失败:', error);
            wx.showToast({
              title: error.message || '签退失败',
              icon: 'error'
            });
          } finally {
            this.setData({ loading: false });
          }
        }
      }
    });
  },

  /**
   * 查看签到码
   */
  onShowCheckCode() {
    const { todayCheckCode } = this.data;
    if (todayCheckCode && todayCheckCode.code) {
      wx.showModal({
        title: '今日签到码',
        content: `签到码：${todayCheckCode.code}\n\n请在自习室内使用此签到码进行签到`,
        showCancel: false,
        confirmText: '知道了'
      });
    } else {
      wx.showToast({
        title: '暂无签到码',
        icon: 'none'
      });
    }
  },

  /**
   * 返回上一页
   */
  onBack() {
    wx.navigateBack();
  }
}); 