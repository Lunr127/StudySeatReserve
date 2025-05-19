const { studyRoomApi } = require('../../../../utils/api');
const { getUserInfo } = require('../../../../utils/auth');

Page({
  data: {
    isEdit: false,
    roomId: null,
    formData: {
      name: '',
      building: '',
      floor: '',
      roomNumber: '',
      location: '',
      capacity: '',
      openTime: '08:00',
      closeTime: '22:00',
      belongsTo: '全校',
      isActive: 1,
      adminId: null,
      adminName: '',
      description: ''
    },
    belongsToOptions: ['全校', '计算机学院', '文学院', '理学院', '工学院', '经济学院'],
    belongsToIndex: 0,
    adminOptions: [],
    adminIndex: -1
  },
  
  onLoad: function(options) {
    // 检查是否为管理员
    this.checkIsAdmin();
    
    // 加载管理员列表
    this.loadAdminList();
    
    // 如果有ID参数，则为编辑模式
    if (options.id) {
      this.setData({
        isEdit: true,
        roomId: String(options.id)  // 确保ID以字符串形式存储
      });
      
      // 设置导航栏标题
      wx.setNavigationBarTitle({
        title: '编辑自习室'
      });
      
      // 加载自习室详情
      this.loadRoomDetail(String(options.id));  // 确保ID以字符串形式传递
    } else {
      // 新增模式
      wx.setNavigationBarTitle({
        title: '新增自习室'
      });
    }
  },
  
  // 检查用户是否为管理员
  checkIsAdmin: function() {
    getUserInfo().then(userInfo => {
      if (!userInfo || userInfo.userType !== 1) {
        wx.showToast({
          title: '您没有管理员权限',
          icon: 'none'
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    }).catch(err => {
      console.error('获取用户信息失败', err);
      wx.showToast({
        title: '获取用户信息失败',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    });
  },
  
  // 加载管理员列表
  loadAdminList: function() {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });

    // 调用API获取管理员列表
    const { adminApi } = require('../../../../utils/api');
    adminApi.getAdminList()
      .then(res => {
        wx.hideLoading();
        
        if (res.code === 200 && res.data) {
          // 格式化管理员数据
          const adminList = res.data.map(admin => ({
            id: admin.id,
            name: admin.realName || `管理员(ID:${admin.id})`
          }));
          
          this.setData({
            adminOptions: adminList
          });

          // 如果是编辑模式，且有管理员ID，设置对应的索引
          if (this.data.isEdit && this.data.formData.adminId) {
            const adminIndex = adminList.findIndex(item => item.id === this.data.formData.adminId);
            if (adminIndex !== -1) {
              this.setData({
                adminIndex: adminIndex
              });
            }
          }
        } else {
          this.setData({
            adminOptions: []
          });
          console.error('获取管理员列表失败', res);
        }
      })
      .catch(err => {
        wx.hideLoading();
        console.error('获取管理员列表失败', err);
        this.setData({
          adminOptions: []
        });
      });
  },
  
  // 加载自习室详情
  loadRoomDetail: function(id) {
    wx.showLoading({
      title: '加载中...',
      mask: true
    });
    
    studyRoomApi.getStudyRoomDetail(String(id))
      .then(res => {
        wx.hideLoading();
        
        if (res.code === 200 && res.data) {
          const roomData = res.data;
          
          // 格式化时间显示
          if (roomData.openTime && typeof roomData.openTime === 'string') {
            roomData.openTime = roomData.openTime.substring(0, 5);
          }
          if (roomData.closeTime && typeof roomData.closeTime === 'string') {
            roomData.closeTime = roomData.closeTime.substring(0, 5);
          }
          
          // 设置归属下拉索引
          const belongsToIndex = this.data.belongsToOptions.findIndex(item => item === roomData.belongsTo);
          
          // 设置管理员下拉索引
          const adminIndex = this.data.adminOptions.findIndex(item => item.id === roomData.adminId);
          
          this.setData({
            formData: {
              ...roomData
            },
            belongsToIndex: belongsToIndex !== -1 ? belongsToIndex : 0,
            adminIndex: adminIndex !== -1 ? adminIndex : -1
          });
        } else {
          wx.showToast({
            title: '自习室不存在或已删除',
            icon: 'none'
          });
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        }
      })
      .catch(err => {
        wx.hideLoading();
        console.error('加载自习室详情失败', err);
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      });
  },
  
  // 绑定开放时间选择器change事件
  bindOpenTimeChange: function(e) {
    this.setData({
      'formData.openTime': e.detail.value
    });
  },
  
  // 绑定关闭时间选择器change事件
  bindCloseTimeChange: function(e) {
    this.setData({
      'formData.closeTime': e.detail.value
    });
  },
  
  // 绑定归属选择器change事件
  bindBelongsToChange: function(e) {
    const index = e.detail.value;
    const value = this.data.belongsToOptions[index];
    
    this.setData({
      belongsToIndex: index,
      'formData.belongsTo': value
    });
  },
  
  // 绑定管理员选择器change事件
  bindAdminChange: function(e) {
    const index = e.detail.value;
    const admin = this.data.adminOptions[index];
    
    this.setData({
      adminIndex: index,
      'formData.adminId': admin.id,
      'formData.adminName': admin.name
    });
  },
  
  // 绑定是否开放switch change事件
  bindIsActiveChange: function(e) {
    this.setData({
      'formData.isActive': e.detail.value ? 1 : 0
    });
  },
  
  // 取消编辑
  cancelEdit: function() {
    wx.showModal({
      title: '提示',
      content: '确定要放弃编辑吗？',
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack();
        }
      }
    });
  },
  
  // 提交表单
  submitForm: function(e) {
    const formValues = e.detail.value;
    const formData = this.data.formData;
    
    // 合并表单值和已保存的值
    const submitData = {
      ...formData,
      ...formValues
    };
    
    // 表单验证
    if (!submitData.name) {
      return this.showError('自习室名称不能为空');
    }
    
    if (!submitData.building) {
      return this.showError('所在建筑不能为空');
    }
    
    if (!submitData.location) {
      return this.showError('位置描述不能为空');
    }
    
    if (!submitData.capacity) {
      return this.showError('座位容量不能为空');
    }
    
    if (isNaN(submitData.capacity) || parseInt(submitData.capacity) <= 0) {
      return this.showError('座位容量必须为正整数');
    }
    
    if (!submitData.openTime) {
      return this.showError('开放时间(开始)不能为空');
    }
    
    if (!submitData.closeTime) {
      return this.showError('开放时间(结束)不能为空');
    }
    
    // 确保capacity是数字
    submitData.capacity = parseInt(submitData.capacity);
    
    // 提交数据
    wx.showLoading({
      title: '保存中...',
      mask: true
    });
    
    if (this.data.isEdit) {
      // 编辑模式
      studyRoomApi.updateStudyRoom(String(this.data.roomId), submitData)
        .then(res => {
          this.handleSubmitResponse(res);
        })
        .catch(err => {
          this.handleSubmitError(err);
        });
    } else {
      // 新增模式
      studyRoomApi.createStudyRoom(submitData)
        .then(res => {
          this.handleSubmitResponse(res);
        })
        .catch(err => {
          this.handleSubmitError(err);
        });
    }
  },
  
  // 处理提交响应
  handleSubmitResponse: function(res) {
    wx.hideLoading();
    
    if (res.code === 200) {
      wx.showToast({
        title: this.data.isEdit ? '更新成功' : '创建成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } else {
      wx.showToast({
        title: res.message || '操作失败',
        icon: 'none'
      });
    }
  },
  
  // 处理提交错误
  handleSubmitError: function(err) {
    wx.hideLoading();
    console.error('提交表单失败', err);
    wx.showToast({
      title: '保存失败，请重试',
      icon: 'none'
    });
  },
  
  // 显示错误提示
  showError: function(message) {
    wx.showToast({
      title: message,
      icon: 'none'
    });
    return false;
  }
}) 