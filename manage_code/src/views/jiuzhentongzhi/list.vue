<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							医生账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.yishengzhanghao" placeholder="医生账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送状态：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.fasongzhuangtai" placeholder="发送状态" clearable>
								<el-option label="待发送" value="0"></el-option>
								<el-option label="发送成功" value="1"></el-option>
								<el-option label="发送失败" value="2"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							通知类型：
						</div>
						<div class="search_box">
							<el-select v-model="searchQuery.tongzhileixing" placeholder="通知类型" clearable>
								<el-option label="预约确认" value="1"></el-option>
								<el-option label="就诊前提醒" value="2"></el-option>
								<el-option label="检查准备事项" value="3"></el-option>
								<el-option label="其他提醒" value="4"></el-option>
							</el-select>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
						<el-button class="search_btn" @click="resetSearch()" size="small">重置</el-button>
					</div>
				</el-form>
				<div class="statistics_view" v-if="statisticsData">
					<el-card class="statistics_card">
						<div class="statistics_item">
							<div class="statistics_label">待发送</div>
							<div class="statistics_value pending">{{statisticsData.pendingCount || 0}}</div>
						</div>
					</el-card>
					<el-card class="statistics_card">
						<div class="statistics_item">
							<div class="statistics_label">发送成功</div>
							<div class="statistics_value success">{{statisticsData.successCount || 0}}</div>
						</div>
					</el-card>
					<el-card class="statistics_card">
						<div class="statistics_item">
							<div class="statistics_label">发送失败</div>
							<div class="statistics_value failed">{{statisticsData.failedCount || 0}}</div>
						</div>
					</el-card>
					<el-card class="statistics_card">
						<div class="statistics_item">
							<div class="statistics_label">总计</div>
							<div class="statistics_value total">{{statisticsData.totalCount || 0}}</div>
						</div>
					</el-card>
				</div>
				<div class="btn_view">
					<el-button class="add_btn" type="success" @click="addClick" v-if="btnAuth('jiuzhentongzhi','新增')">
						<i class="iconfont icon-xinzeng1"></i>
						新增
					</el-button>
					<el-button class="del_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('jiuzhentongzhi','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
					<el-button class="refresh_btn" type="warning" @click="getStatistics">
						<i class="iconfont icon-tongji"></i>
						刷新统计
					</el-button>
					<el-button class="failed_btn" type="info" @click="showFailedList">
						<i class="iconfont icon-shibai"></i>
						失败通知
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('jiuzhentongzhi','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibianhao"
					label="通知编号">
					<template #default="scope">
						{{scope.row.tongzhibianhao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="yishengzhanghao"
					label="医生账号">
					<template #default="scope">
						{{scope.row.yishengzhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="用户账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhileixing"
					label="通知类型">
					<template #default="scope">
						<el-tag v-if="scope.row.tongzhileixing==1" type="success">预约确认</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==2" type="warning">就诊前提醒</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==3" type="info">检查准备事项</el-tag>
						<el-tag v-else>其他提醒</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongzhuangtai"
					label="发送状态">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongzhuangtai==0" type="info">待发送</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==1" type="success">发送成功</el-tag>
						<el-tag v-else-if="scope.row.fasongzhuangtai==2" type="danger">发送失败</el-tag>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongshijian"
					label="发送时间">
					<template #default="scope">
						{{scope.row.fasongshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="chongshicishu"
					label="重试次数">
					<template #default="scope">
						<el-tag v-if="scope.row.chongshicishu>0" type="warning">{{scope.row.chongshicishu}}</el-tag>
						<span v-else>-</span>
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="jiuzhenshijian"
					label="就诊时间">
					<template #default="scope">
						{{scope.row.jiuzhenshijian}}
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhibeizhu"
					label="通知备注">
					<template #default="scope">
						<el-tooltip :content="scope.row.tongzhibeizhu" placement="top">
							<div class="text_ellipsis">{{scope.row.tongzhibeizhu}}</div>
						</el-tooltip>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="350" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('jiuzhentongzhi','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="edit_btn" type="primary" @click="editClick(scope.row.id)" v-if=" btnAuth('jiuzhentongzhi','修改')">
							<i class="iconfont icon-xiugai5"></i>
							修改
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('jiuzhentongzhi','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
						<el-button class="resend_btn" type="warning" @click="resendClick(scope.row.id)" v-if="scope.row.fasongzhuangtai==2 && btnAuth('jiuzhentongzhi','重发')">
							<i class="iconfont icon-zhongfa"></i>
							重发
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
		
		<!-- 失败通知列表弹窗 -->
		<el-dialog title="失败通知列表" v-model="failedDialogVisible" width="80%">
			<div class="failed_search">
				<el-form :inline="true" :model="failedSearchQuery">
					<el-form-item label="用户账号">
						<el-input v-model="failedSearchQuery.zhanghao" placeholder="请输入用户账号" clearable></el-input>
					</el-form-item>
					<el-form-item label="开始时间">
						<el-date-picker v-model="failedSearchQuery.startTime" type="datetime" placeholder="选择开始时间"></el-date-picker>
					</el-form-item>
					<el-form-item label="结束时间">
						<el-date-picker v-model="failedSearchQuery.endTime" type="datetime" placeholder="选择结束时间"></el-date-picker>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="searchFailedList">搜索</el-button>
						<el-button @click="resetFailedSearch">重置</el-button>
					</el-form-item>
				</el-form>
			</div>
			<el-table :data="failedList" border v-loading="failedLoading">
				<el-table-column type="selection" width="55"></el-table-column>
				<el-table-column prop="tongzhibianhao" label="通知编号" min-width="140"></el-table-column>
				<el-table-column prop="zhanghao" label="用户账号" min-width="120"></el-table-column>
				<el-table-column prop="shouji" label="手机" min-width="120"></el-table-column>
				<el-table-column prop="tongzhileixing" label="通知类型" min-width="120">
					<template #default="scope">
						<el-tag v-if="scope.row.tongzhileixing==1" type="success">预约确认</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==2" type="warning">就诊前提醒</el-tag>
						<el-tag v-else-if="scope.row.tongzhileixing==3" type="info">检查准备事项</el-tag>
						<el-tag v-else>其他提醒</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="chongshicishu" label="重试次数" width="100">
					<template #default="scope">
						<el-tag type="warning">{{scope.row.chongshicishu}}/3</el-tag>
					</template>
				</el-table-column>
				<el-table-column prop="shibaiyuanyin" label="失败原因" min-width="200">
					<template #default="scope">
						<el-tooltip :content="scope.row.shibaiyuanyin" placement="top">
							<div class="text_ellipsis">{{scope.row.shibaiyuanyin}}</div>
						</el-tooltip>
					</template>
				</el-table-column>
				<el-table-column prop="xiacichongshishijian" label="下次重试时间" min-width="160"></el-table-column>
				<el-table-column label="操作" width="200" fixed="right">
					<template #default="scope">
						<el-button type="warning" size="small" @click="resendClick(scope.row.id)">重发</el-button>
						<el-button type="info" size="small" @click="markAsHandled(scope.row.id)">标记已处理</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="failedTotal"
				:page-size="failedQuery.limit"
                v-model:current-page="failedQuery.page"
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="failedSizeChange"
				@current-change="failedCurrentChange" />
		</el-dialog>
	</div>
</template>
<script setup>
	import axios from 'axios'
    import moment from "moment"
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox,
		ElMessage
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	//基础信息

	const tableName = 'jiuzhentongzhi'
	const formName = '就诊通知'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
		getStatistics()
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.toggleRowSelection(row)
		})
	}
	//统计信息
	const statisticsData = ref(null)
	const getStatistics = () => {
		context.$http({
			url: `${tableName}/statistics`,
			method: 'get'
		}).then(res => {
			if(res.data.code==0){
				statisticsData.value = res.data.data
			}
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.yishengzhanghao&&searchQuery.value.yishengzhanghao!=''){
			params['yishengzhanghao'] = '%' + searchQuery.value.yishengzhanghao + '%'
		}
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.fasongzhuangtai&&searchQuery.value.fasongzhuangtai!=''){
			params['fasongzhuangtai'] = searchQuery.value.fasongzhuangtai
		}
		if(searchQuery.value.tongzhileixing&&searchQuery.value.tongzhileixing!=''){
			params['tongzhileixing'] = searchQuery.value.tongzhileixing
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//重置搜索
	const resetSearch = () => {
		searchQuery.value = {}
		listQuery.value.page = 1
		getList()
	}
	//删
	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
					getStatistics()
				})
			})
		}).catch(_ => {})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
		getStatistics()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'edit')
			return
		}
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}

	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}
	
	// 重发通知
	const resendClick = (id) => {
		ElMessageBox.confirm('确定要重新发送该通知吗？', '提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(() => {
			context.$http({
				url: `${tableName}/resend/${id}`,
				method: 'get'
			}).then(res => {
				if(res.data.code==0){
					ElMessage.success('重发成功')
					getList()
					getStatistics()
					if(failedDialogVisible.value){
						getFailedList()
					}
				}else{
					ElMessage.error(res.data.msg || '重发失败')
				}
			})
		}).catch(() => {})
	}
	
	// 失败通知弹窗
	const failedDialogVisible = ref(false)
	const failedList = ref([])
	const failedLoading = ref(false)
	const failedTotal = ref(0)
	const failedQuery = ref({
		page: 1,
		limit: 10
	})
	const failedSearchQuery = ref({})
	
	const showFailedList = () => {
		failedDialogVisible.value = true
		getFailedList()
	}
	
	const getFailedList = () => {
		failedLoading.value = true
		let params = JSON.parse(JSON.stringify(failedQuery.value))
		if(failedSearchQuery.value.zhanghao){
			params['zhanghao'] = failedSearchQuery.value.zhanghao
		}
		if(failedSearchQuery.value.startTime){
			params['startTime'] = moment(failedSearchQuery.value.startTime).format('YYYY-MM-DD HH:mm:ss')
		}
		if(failedSearchQuery.value.endTime){
			params['endTime'] = moment(failedSearchQuery.value.endTime).format('YYYY-MM-DD HH:mm:ss')
		}
		context.$http({
			url: `${tableName}/failedList`,
			method: 'get',
			params: params
		}).then(res => {
			failedLoading.value = false
			failedList.value = res.data.data.list
			failedTotal.value = Number(res.data.data.total)
		})
	}
	
	const searchFailedList = () => {
		failedQuery.value.page = 1
		getFailedList()
	}
	
	const resetFailedSearch = () => {
		failedSearchQuery.value = {}
		failedQuery.value.page = 1
		getFailedList()
	}
	
	const failedSizeChange = (size) => {
		failedQuery.value.limit = size
		getFailedList()
	}
	
	const failedCurrentChange = (page) => {
		failedQuery.value.page = page
		getFailedList()
	}
	
	// 标记已处理
	const markAsHandled = (id) => {
		ElMessageBox.prompt('请输入处理备注', '标记已处理', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			inputPlaceholder: '请输入处理备注信息'
		}).then(({ value }) => {
			context.$http({
				url: `${tableName}/updateStatus`,
				method: 'get',
				params: {
					id: id,
					status: 1,
					remark: value || '管理员手动标记为已处理'
				}
			}).then(res => {
				if(res.data.code==0){
					ElMessage.success('标记成功')
					getFailedList()
					getStatistics()
				}
			})
		}).catch(() => {})
	}
	
	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>

	// 统计面板
	.statistics_view {
		display: flex;
		gap: 15px;
		margin-bottom: 20px;
		flex-wrap: wrap;
		
		.statistics_card {
			flex: 1;
			min-width: 150px;
			
			.statistics_item {
				text-align: center;
				
				.statistics_label {
					font-size: 14px;
					color: #666;
					margin-bottom: 10px;
				}
				
				.statistics_value {
					font-size: 28px;
					font-weight: bold;
					
					&.pending {
						color: #909399;
					}
					&.success {
						color: #67C23A;
					}
					&.failed {
						color: #F56C6C;
					}
					&.total {
						color: #409EFF;
					}
				}
			}
		}
	}
	
	// 文字省略
	.text_ellipsis {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		max-width: 200px;
	}

	// 操作盒子
	.list_search_view {
		// 搜索盒子
		.search_form {
			// 子盒子
			.search_view {
				// 搜索label
				.search_label {
				}
				// 搜索item
				.search_box {
					// 输入框
					:deep(.search_inp) {
					}
				}
			}
			// 搜索按钮盒子
			.search_btn_view {
				// 搜索按钮
				.search_btn {
				}
				// 搜索按钮-悬浮
				.search_btn:hover {
				}
			}
		}
		//头部按钮盒子
		.btn_view {
			// 其他
			:deep(.el-button--default){
			}
			// 其他-悬浮
			:deep(.el-button--default:hover){
			}
			// 新增
			:deep(.el-button--success){
			}
			// 新增-悬浮
			:deep(.el-button--success:hover){
			}
			// 删除
			:deep(.el-button--danger){
			}
			// 删除-悬浮
			:deep(.el-button--danger:hover){
			}
			// 统计
			:deep(.el-button--warning){
			}
			// 统计-悬浮
			:deep(.el-button--warning:hover){
			}
		}
	}
	// 表格样式
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							// 编辑
							.el-button--primary {
							}
							// 编辑-悬浮
							.el-button--primary:hover {
							}
							// 详情
							.el-button--info {
							}
							// 详情-悬浮
							.el-button--info:hover {
							}
							// 删除
							.el-button--danger {
							}
							// 删除-悬浮
							.el-button--danger:hover {
							}
							// 跨表
							.el-button--success {
							}
							// 跨表-悬浮
							.el-button--success:hover {
							}
							// 操作
							.el-button--warning {
							}
							// 操作-悬浮
							.el-button--warning:hover {
							}
						}
					}
				}
				tr:hover {
					td {
					}
				}
			}
		}
	}
	// 分页器
	.el-pagination {
		// 总页码
		:deep(.el-pagination__total) {
		}
		// 上一页
		:deep(.btn-prev) {
		}
		// 下一页
		:deep(.btn-next) {
		}
		// 上一页禁用
		:deep(.btn-prev:disabled) {
		}
		// 下一页禁用
		:deep(.btn-next:disabled) {
		}
		// 页码
		:deep(.el-pager) {
			// 数字
			.number {
			}
			// 数字悬浮
			.number:hover {
			}
			// 选中
			.number.is-active {
			}
		}
		// sizes
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		// 跳页
		:deep(.el-pagination__jump) {
			// 输入框
			.el-input {
			}
		}
	}
	
	// 失败通知弹窗
	.failed_search {
		margin-bottom: 20px;
	}
</style>
