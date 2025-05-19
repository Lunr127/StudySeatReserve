Component({
  /**
   * 组件的属性列表
   */
  properties: {
    seats: {
      type: Array,
      value: []
    },
    maxRow: {
      type: Number,
      value: 0
    },
    maxColumn: {
      type: Number,
      value: 0
    },
    selectable: {
      type: Boolean,
      value: true
    },
    selectedSeatId: {
      type: Number,
      value: null
    },
    size: {
      type: String,
      value: 'normal' // normal, large, small
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    seatMap: [],
    seatSize: 60,
    seatGap: 10
  },

  observers: {
    'seats, maxRow, maxColumn, size': function(seats, maxRow, maxColumn, size) {
      if (seats.length > 0) {
        this.initSeatMap(seats, maxRow, maxColumn);
        this.setSeatSize(size);
      }
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    // 初始化座位映射
    initSeatMap: function(seats, maxRow, maxColumn) {
      // 如果没有传入最大行列数，则计算
      if (!maxRow || !maxColumn) {
        seats.forEach(seat => {
          maxRow = Math.max(maxRow, seat.rowNumber);
          maxColumn = Math.max(maxColumn, seat.columnNumber);
        });
      }

      // 创建空座位映射
      const seatMap = [];
      for (let i = 0; i < maxRow; i++) {
        const row = [];
        for (let j = 0; j < maxColumn; j++) {
          row.push(null);
        }
        seatMap.push(row);
      }

      // 填充座位
      seats.forEach(seat => {
        if (seat.rowNumber > 0 && seat.rowNumber <= maxRow && 
            seat.columnNumber > 0 && seat.columnNumber <= maxColumn) {
          seatMap[seat.rowNumber - 1][seat.columnNumber - 1] = seat;
        }
      });

      this.setData({
        seatMap,
        maxRow,
        maxColumn
      });
    },

    // 设置座位大小
    setSeatSize: function(size) {
      let seatSize = 60; // 默认大小
      let seatGap = 10; // 默认间距

      switch (size) {
        case 'small':
          seatSize = 40;
          seatGap = 5;
          break;
        case 'large':
          seatSize = 80;
          seatGap = 15;
          break;
        default:
          seatSize = 60;
          seatGap = 10;
      }

      this.setData({
        seatSize,
        seatGap
      });
    },

    // 座位点击事件
    onSeatTap: function(e) {
      const { row, col } = e.currentTarget.dataset;
      const seat = this.data.seatMap[row][col];
      
      if (!seat) return; // 空位置
      
      if (!this.data.selectable) return; // 不可选择
      
      if (seat.status === 0) return; // 停用的座位不能选
      
      if (seat.isAvailable === false) return; // 不可用的座位不能选
      
      this.triggerEvent('seatselect', {
        seat
      });
    },

    // 获取座位状态类
    getSeatClass: function(seat) {
      if (!seat) return 'seat-null';
      
      let cls = 'seat ';
      
      // 状态类
      if (seat.status === 0) {
        cls += 'seat-disabled ';
      } else if (seat.isAvailable === false) {
        cls += 'seat-occupied ';
      } else {
        cls += 'seat-available ';
      }
      
      // 特性类
      if (seat.hasPower === 1) cls += 'seat-power ';
      if (seat.isWindow === 1) cls += 'seat-window ';
      if (seat.isCorner === 1) cls += 'seat-corner ';
      
      // 选中类
      if (this.data.selectedSeatId === seat.id) {
        cls += 'seat-selected ';
      }
      
      return cls;
    }
  }
}) 