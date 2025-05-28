const app = getApp();
const { userApi, reservationApi, studyRoomApi } = require('../../utils/api');

Page({
  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    hasLogin: false,
    studyRoomList: [], // 自习室列表
    // 轮播图数据
    bannerList: [
      {
        id: 1,
        image: '/images/banner1.png',
        title: '图书馆自习室'
      },
      {
        id: 2,
        image: '/images/banner2.png',
        title: '教学楼自习室'
      }
    ],
    // 统计数据
    statistics: {
      availableRooms: 0,
      availableSeats: 0,
      reservationCount: 0
    },
    // 最近预约
    recentReservations: []
  },

  /**
   * 跳转到自习室列表
   */
  goToStudyRooms: function() {
    wx.switchTab({
      url: '/pages/study-rooms/study-rooms',
    });
  },

  /**
   * 跳转到登录页
   */
  goToLogin: function() {
    wx.navigateTo({
      url: '/pages/login/login',
    });
  },

  /**
   * 跳转到用户中心
   */
  goToUserCenter: function() {
    wx.switchTab({
      url: '/pages/user-center/user-center',
    });
  },

  /**
   * 跳转到座位预约页
   */
  goToSeatReservation: function(e) {
    const roomId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/seat-reservation/seat-selection/seat-selection?roomId=' + roomId,
    });
  },

  /**
   * 跳转到扫码签到页面
   */
  goToScanCheckIn: function() {
    // 检查是否已登录
    if (!this.data.hasLogin) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      this.goToLogin();
      return;
    }
    
    // 跳转到签到页面
    wx.navigateTo({
      url: '/pages/check-in/check-in'
    });
  },

  /**
   * 跳转到我的收藏页面
   */
  goToFavorites: function() {
    // 检查是否已登录
    if (!this.data.hasLogin) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      this.goToLogin();
      return;
    }
    
    // 跳转到收藏页面
    wx.navigateTo({
      url: '/pages/user-center/my-favorites/my-favorites'
    });
  },

  /**
   * 获取自习室列表
   */
  getStudyRoomList: function() {
    // 调用后端API获取自习室列表，获取更多以便筛选
    studyRoomApi.getStudyRooms({
      current: 1,
      size: 10, // 获取更多数据以便筛选不同类型
      isActive: 1 // 只获取开放的自习室
    }).then(res => {
      if (res.code === 200) {
        const allStudyRooms = res.data.records || [];
        
        // 按建筑分组，优先选择不同建筑的自习室
        const roomsByBuilding = {};
        allStudyRooms.forEach(room => {
          const building = room.building || '未知建筑';
          if (!roomsByBuilding[building]) {
            roomsByBuilding[building] = [];
          }
          roomsByBuilding[building].push(room);
        });
        
        // 选择代表性的自习室：优先图书馆和教学楼
        const selectedRooms = [];
        const buildingPriority = ['图书馆', '工学院教学楼', '计算机学院大楼'];
        
        // 按优先级选择不同建筑的自习室
        buildingPriority.forEach(building => {
          if (roomsByBuilding[building] && roomsByBuilding[building].length > 0 && selectedRooms.length < 2) {
            selectedRooms.push(roomsByBuilding[building][0]); // 选择每个建筑的第一个
          }
        });
        
        // 如果还没选够2个，从其他建筑补充
        for (const building in roomsByBuilding) {
          if (selectedRooms.length >= 2) break;
          if (!buildingPriority.includes(building) && roomsByBuilding[building].length > 0) {
            selectedRooms.push(roomsByBuilding[building][0]);
          }
        }
        
        // 如果还是不够，直接取前2个
        if (selectedRooms.length < 2) {
          selectedRooms.push(...allStudyRooms.slice(0, 2 - selectedRooms.length));
        }
        
        // 转换数据格式以适配前端显示
        const studyRoomList = selectedRooms.map(room => {
          // 计算可用座位数：总座位数 - 已使用座位数
          const totalSeats = room.capacity || 0;
          const usedSeats = room.usedCapacity || 0;
          const availableSeats = Math.max(0, totalSeats - usedSeats);
          
          // 格式化时间显示：处理各种时间格式
          const formatTime = (timeData) => {
            if (!timeData) return '';
            
            // 如果是数组格式（LocalTime的JSON序列化结果）：[8, 0] 或 [8, 30]
            if (Array.isArray(timeData)) {
              const hour = timeData[0] || 0;
              const minute = timeData[1] || 0;
              return `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
            }
            
            // 如果是字符串格式
            const timeString = String(timeData);
            
            // 如果包含冒号，截取前5位（HH:MM）
            if (timeString.includes(':')) {
              return timeString.substring(0, 5);
            }
            
            // 如果是其他格式，直接返回
            return timeString;
          };
          
          // 根据建筑类型选择合适的图片
          const getImageByBuilding = (building) => {
            if (building && building.includes('图书馆')) {
              return '/images/library.png';
            } else if (building && (building.includes('教学楼') || building.includes('学院'))) {
              return '/images/classroom.png';
            }
            return '/images/library.png'; // 默认图片
          };
          
          return {
            id: room.id,
            name: room.name,
            location: room.location,
            availableSeats: availableSeats,
            totalSeats: totalSeats,
            openTime: room.openTime && room.closeTime ? 
              `${formatTime(room.openTime)}-${formatTime(room.closeTime)}` : '08:00-22:00',
            image: getImageByBuilding(room.building)
          };
        });
        
        this.setData({
          studyRoomList: studyRoomList,
          statistics: {
            availableRooms: allStudyRooms.length, // 显示所有可用自习室数量
            availableSeats: studyRoomList.reduce((total, room) => total + room.availableSeats, 0),
            reservationCount: 256 // 暂时使用固定值，后续可以通过API获取
          }
        });
      } else {
        console.error('获取自习室列表失败:', res.message);
        // 如果获取失败，使用默认的模拟数据
        this.setDefaultStudyRoomList();
      }
    }).catch(err => {
      console.error('获取自习室列表错误:', err);
      // 如果网络错误，使用默认的模拟数据
      this.setDefaultStudyRoomList();
    });
  },

  /**
   * 设置默认自习室列表（备用方案）
   */
  setDefaultStudyRoomList: function() {
    this.setData({
      studyRoomList: [
        {
          id: 1,
          name: '中心图书馆一楼自习室',
          location: '中心图书馆一楼',
          availableSeats: 45,
          totalSeats: 100,
          openTime: '08:00-22:00',
          image: '/images/library.png'
        },
        {
          id: 2,
          name: '工学院自习室',
          location: '工学院楼3楼',
          availableSeats: 30,
          totalSeats: 80,
          openTime: '08:30-21:30',
          image: '/images/classroom.png'
        }
      ],
      statistics: {
        availableRooms: 2,
        availableSeats: 75,
        reservationCount: 256
      }
    });
  },

  /**
   * 获取最近预约
   */
  getRecentReservations: function() {
    if (this.data.hasLogin) {
      // 调用后端API获取当前有效预约
      reservationApi.getCurrentReservations().then(res => {
        if (res.code === 200) {
          // 转换数据格式以适配前端显示
          const recentReservations = res.data.map(reservation => ({
            id: reservation.id,
            roomName: reservation.studyRoomName,
            seatNumber: reservation.seatNumber,
            date: reservation.startTime ? reservation.startTime.split(' ')[0] : '',
            time: reservation.startTime && reservation.endTime ? 
              reservation.startTime.split(' ')[1].substring(0, 5) + '-' + 
              reservation.endTime.split(' ')[1].substring(0, 5) : '',
            status: reservation.statusText || this.getStatusText(reservation.status),
            startTime: reservation.startTime,
            endTime: reservation.endTime,
            canCancel: reservation.canCancel,
            canExtend: reservation.canExtend,
            canCheckIn: reservation.canCheckIn,
            hasCheckedIn: reservation.hasCheckedIn
          }));
          
          this.setData({
            recentReservations: recentReservations
          });
        } else {
          console.error('获取最近预约失败:', res.message);
          // 如果获取失败，设置为空数组
          this.setData({
            recentReservations: []
          });
        }
      }).catch(err => {
        console.error('获取最近预约错误:', err);
        // 如果网络错误，设置为空数组
        this.setData({
          recentReservations: []
        });
      });
    }
  },

  /**
   * 获取状态文字
   */
  getStatusText: function(status) {
    const statusMap = {
      0: '已取消',
      1: '待签到',
      2: '使用中',
      3: '已完成',
      4: '已违约'
    };
    return statusMap[status] || '未知状态';
  },

  /**
   * 检查登录状态并获取用户信息
   */
  checkLoginStatus: function() {
    // 检查全局数据中是否有token
    const hasLogin = app.checkLogin();
    this.setData({
      hasLogin: hasLogin
    });
    
    if (hasLogin) {
      // 如果已登录，从后端获取用户信息
      this.getUserInfo();
      // 获取最近预约
      this.getRecentReservations();
    }
  },

  /**
   * 获取用户信息
   */
  getUserInfo: function() {
    userApi.getUserInfo().then(res => {
      if (res.code === 200) {
        const userInfo = res.data;
        this.setData({
          userInfo: {
            nickName: userInfo.realName || userInfo.username,
            avatarUrl: userInfo.avatar || '/images/avatar.png',
            realName: userInfo.realName,
            username: userInfo.username,
            userType: userInfo.userType,
            phone: userInfo.phone,
            email: userInfo.email
          }
        });
        
        // 更新全局用户信息
        app.globalData.userInfo = userInfo;
        wx.setStorageSync('userInfo', userInfo);
      } else {
        console.error('获取用户信息失败:', res.message);
        // 如果获取失败，使用本地缓存的用户信息
        const cachedUserInfo = wx.getStorageSync('userInfo');
        if (cachedUserInfo) {
          this.setData({
            userInfo: {
              nickName: cachedUserInfo.realName || cachedUserInfo.username,
              avatarUrl: cachedUserInfo.avatar || '/images/avatar.png',
              realName: cachedUserInfo.realName,
              username: cachedUserInfo.username,
              userType: cachedUserInfo.userType,
              phone: cachedUserInfo.phone,
              email: cachedUserInfo.email
            }
          });
        }
      }
    }).catch(err => {
      console.error('获取用户信息错误:', err);
      // 如果网络错误，使用本地缓存的用户信息
      const cachedUserInfo = wx.getStorageSync('userInfo');
      if (cachedUserInfo) {
        this.setData({
          userInfo: {
            nickName: cachedUserInfo.realName || cachedUserInfo.username,
            avatarUrl: cachedUserInfo.avatar || '/images/avatar.png',
            realName: cachedUserInfo.realName,
            username: cachedUserInfo.username,
            userType: cachedUserInfo.userType,
            phone: cachedUserInfo.phone,
            email: cachedUserInfo.email
          }
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.checkLoginStatus();
    this.getStudyRoomList();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    // 每次页面显示时更新登录状态和数据
    this.checkLoginStatus();
    this.getStudyRoomList();
    if (this.data.hasLogin) {
      this.getRecentReservations();
    }
  }
}); 