<template>
  <div class="fillcontain">
    <div class="container_wrap">
      <div class="gaode_container" ref="gaodeContainer"></div>
    </div>
  </div>
</template>

<script>
import AMap from "AMap"; // 引入高德地图
export default {
  props: {
    lng: null,
    lat: null,
    initPosition: false
  },
  data() {
    return {
      searchKeyWord: ""
    };
  },
  created() {},
  mounted() {
    let that = this;
    let container = this.$refs.gaodeContainer;
    let search = this.$refs.gaodeSearch;
    var map = new AMap.Map(container, {
      resizeEnable: true
    });
    map.plugin(
      [
        "AMap.ToolBar",
        "AMap.Autocomplete",
        "AMap.ContextMenu",
        "AMap.Marker",
        "AMap.LngLat",
        "AMap.Geocoder"
      ],
      function() {
        map.addControl(new AMap.ToolBar());
        var aMapGeocoder = new AMap.Geocoder();
        let contextMenu = new AMap.ContextMenu();
        let marker;
        let contextMenuPositon;
        if (that.initPosition) {
          marker = new AMap.Marker({
            position: new AMap.LngLat(
              that.lng,
              that.lat
            ) // 经纬度对象
          });
          map.add(marker);
        }
        //右键添加Marker标记
        contextMenu.addItem(
          "标记该位置",
          function(e) {
            if (marker) {
              map.remove(marker);
            }
            marker = new AMap.Marker({
              position: new AMap.LngLat(
                contextMenuPositon.getLng(),
                contextMenuPositon.getLat()
              ) // 经纬度对象
            });
            map.add(marker);
          },
          3
        );
        contextMenu.addItem("删除标记", function(e) {
          map.remove(marker);
          marker = null;
        });
        contextMenu.addItem("确认位置", function(e) {
          that
            .$confirm("确认该位置并返回编辑?", "提示", {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning"
            })
            .then(() => {
              if (!marker) {
                that.$message.error("没有选择标记位置");
              }
              let lng = marker.Le.position.lng;
              let lat = marker.Le.position.lat;
              let lnglat = [lng, lat];
              // result为对应的地理位置详细信息
              // 获取marker对象中的地理位置值
              aMapGeocoder.getAddress(lnglat, function(status, result) {
                if (status === "complete" && result.info === "OK") {
                  // result为对应的地理位置详细信息
                  that.$emit(
                    "confirmAddress",
                    marker.Le.position.lng,
                    marker.Le.position.lat,
                    result.regeocode.formattedAddress
                  );
                }
              });
            })
            .catch(() => {});
        });
        contextMenu.addItem("放弃并返回编辑", function(e) {
          that.$emit("giveUpChoose");
        });
        //地图绑定鼠标右击事件——弹出右键菜单
        map.on("rightclick", function(e) {
          contextMenu.open(map, e.lnglat);
          contextMenuPositon = e.lnglat;
        });
        map.addControl(new AMap.ContextMenu());
      }
    );
    map.on("click", this.clickHandler);
  },
  methods: {}
};
</script>

<style lang="less" scope>
@import "../style/common";
@import "../style/mixin";
.container_wrap {
  position: relative;
  width: 100%;
  height: 100%;
  .gaode_container {
    width: 100%;
    height: 100%;
  }
}
.searchForm {
  width: 20%;
  position: absolute;
  top: 0;
  right: 0;
}
</style>
