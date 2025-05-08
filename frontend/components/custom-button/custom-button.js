Component({
  /**
   * 组件的属性列表
   */
  properties: {
    text: {
      type: String,
      value: '按钮'
    },
    type: {
      type: String,
      value: 'primary' // primary, secondary, outline
    },
    size: {
      type: String,
      value: 'default' // small, default, large
    },
    disabled: {
      type: Boolean,
      value: false
    },
    loading: {
      type: Boolean,
      value: false
    },
    block: {
      type: Boolean,
      value: false
    },
    round: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    classes: ''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    handleTap() {
      if (this.data.disabled || this.data.loading) return;
      this.triggerEvent('tap');
    }
  },

  /**
   * 组件生命周期
   */
  lifetimes: {
    attached() {
      const { type, size, disabled, loading, block, round } = this.properties;
      let classes = `btn-${type}`;
      
      if (size !== 'default') {
        classes += ` btn-${size}`;
      }
      
      if (disabled) {
        classes += ' btn-disabled';
      }
      
      if (block) {
        classes += ' btn-block';
      }
      
      if (round) {
        classes += ' btn-round';
      }
      
      this.setData({ classes });
    }
  }
}) 