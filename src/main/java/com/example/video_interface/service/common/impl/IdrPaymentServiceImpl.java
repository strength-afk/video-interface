package com.example.video_interface.service.common.impl;

import com.example.video_interface.model.Order;
import com.example.video_interface.model.User;
import com.example.video_interface.model.UserMoviePurchase;
import com.example.video_interface.model.VipPackage;
import com.example.video_interface.repository.OrderRepository;
import com.example.video_interface.repository.UserRepository;
import com.example.video_interface.repository.UserMoviePurchaseRepository;
import com.example.video_interface.repository.VipPackageRepository;
import com.example.video_interface.service.common.IdrApiClient;
import com.example.video_interface.service.common.IIdrPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.example.video_interface.model.Movie;

/**
 * iDataRiver支付服务实现类，负责下单、支付、查单、回调等业务逻辑
 */
@Slf4j
@Service
public class IdrPaymentServiceImpl implements IIdrPaymentService {

    @Autowired
    private IdrApiClient idrApiClient;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMoviePurchaseRepository userMoviePurchaseRepository;
    
    @Autowired
    private VipPackageRepository vipPackageRepository;

    @Value("${idr.api.projectId}")
    private String buymeabtcProjectId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建商品订单
     */
    @Override
    public Map<String, Object> createOrder(Map<String, Object> params) {
        Map<String, Object> body = new HashMap<>();
        // BUYMEABTC特殊处理
        body.put("projectId", buymeabtcProjectId);
        body.put("orderInfo", params);
        ResponseEntity<String> response = idrApiClient.post("/mapi/order/add", body, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 获取订单支付链接
     */
    @Override
    public Map<String, Object> getPayUrl(Map<String, Object> params) {
        ResponseEntity<String> response = idrApiClient.post("/mapi/order/pay", params, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 查询订单详情
     */
    @Override
    public Map<String, Object> queryOrder(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("id", orderId);
        ResponseEntity<String> response = idrApiClient.get("/mapi/order/info", params, "zh-cn");
        return parseResponse(response);
    }

    /**
     * 处理支付回调
     * 支持的事件类型：
     * - ORDER_COMPLETED: 订单已完成
     * - ORDER_REFUND: 订单已退款
     * - UNDEFINED: 未知事件
     */
    @Override
    @Transactional
    public boolean handleCallback(Map<String, Object> callbackData) {
        try {
            log.info("收到iDataRiver支付回调，数据: {}", callbackData);
            
            // 1. 验证回调数据
            if (callbackData == null || callbackData.isEmpty()) {
                log.error("回调数据为空");
                return false;
            }
            
            // 2. 获取事件类型
            String event = (String) callbackData.get("event");
            if (event == null) {
                log.error("回调数据缺少event字段");
                return false;
            }
            
            log.info("处理iDataRiver回调事件: {}", event);
            
            // 3. 根据事件类型处理
            switch (event) {
                case "ORDER_COMPLETED":
                    return handleOrderCompleted(callbackData);
                case "ORDER_REFUND":
                    return handleOrderRefund(callbackData);
                case "UNDEFINED":
                    log.warn("收到未知事件类型: {}", event);
                    return true; // 未知事件不处理，但返回成功
                default:
                    log.warn("不支持的事件类型: {}", event);
                    return true; // 不支持的事件不处理，但返回成功
            }
            
        } catch (Exception e) {
            log.error("处理iDataRiver回调失败", e);
            return false;
        }
    }
    
    /**
     * 处理订单完成事件
     */
    private boolean handleOrderCompleted(Map<String, Object> callbackData) {
        try {
            // 只从 result.id 获取 orderId
            //{code=0, result={id=687a38f9b5a185839cca9e98, status=DONE, consumer=null, merchant=686680d96333a1e033413282, projectName=久伴视频, projectType=BUYMEABTC, project={id=687621885c04c69a131c995c, name=久伴视频, nameI18n={en=Long-time video}, status=ONLINE, type=BUYMEABTC, cover=https://static.idatariver.com/img-cover-default, seoPath=/project/久伴视频-a83f, isPlatform=false, i18nType=0, desc=久伴视频站, descI18n={en=Long-time video station}}, oriPrice=0.2, price=0.2, buymeabtc={name=, message=, amount=0.2, private=false, btc2usd=119080.725}, anonymous=true, paymentFeeCovered=true, updatedAt=2025-07-18 12:07:21 Z, createdAt=2025-07-18 12:07:21 Z, createdTS=1752840441, dt=20250718, frozenProfit=0.172, frozenTo=1753445260, frozenToDate=2025-07-25, payAt=2025-07-18 12:07:40 Z, payTS=1752840460, paymentFee=0.022, platformFee=0.006, profit=0.172, payDetails={backend=yzf, method=alipay, payTo=platform}, expiredInterval=3600, isConsumer=true, sku={}, mPayments=[{name=crypto, method=crypto, enabled=true, isPlatform=true, paymentFeeCovered=true, ratioRange=0%}, {name=alipay, method=alipay, enabled=true, isPlatform=true, paymentFeeCovered=true, ratioRange=11.0%, desc=场外汇率7.38CNY}, {name=wxpay, method=wxpay, enabled=true, isPlatform=true, paymentFeeCovered=true, ratioRange=11.0%, desc=场外汇率7.43CNY}], mContacts={telegram=@haima999}}, event=ORDER_COMPLETED}
            String orderId = null;
            Object callbackResultObj = callbackData.get("result");
            if (callbackResultObj instanceof Map) {
                orderId = (String) ((Map<?, ?>) callbackResultObj).get("id");
            }
            if (orderId == null) {
                log.error("回调数据缺少orderId字段");
                return false;
            }
            
            
            // 2. 主动查询订单详情，确认订单状态
            Map<String, Object> orderDetail = queryOrder(orderId);
            if (orderDetail == null || orderDetail.isEmpty()) {
                log.error("无法获取订单详情，orderId: {}", orderId);
                return false;
            }
            
            // 3. 检查订单状态
            Object resultObj = orderDetail.get("result");
            if (!(resultObj instanceof Map)) {
                log.error("订单详情格式错误，orderId: {}", orderId);
                return false;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) resultObj;
            String status = (String) result.get("status");
            
            if (!"DONE".equals(status)) {
                log.warn("订单状态不是DONE，orderId: {}, status: {}", orderId, status);
                return true; // 不是完成状态，不处理但返回成功
            }
            
            // 4. 查找本地订单
            Order order = orderRepository.findByOutNo(orderId)
                    .orElse(null);
            
            if (order == null) {
                log.error("未找到对应的本地订单，orderId: {}", orderId);
                return false;
            }
            
            // 5. 检查订单是否已经处理过
            if (order.getOrderStatus() == Order.OrderStatus.PAID) {
                log.info("订单已经处理过，orderId: {}, orderNo: {}", orderId, order.getOrderNo());
                return true;
            }
            
            // 6. 更新订单状态
            order.setOrderStatus(Order.OrderStatus.PAID);
            order.setPaidTime(LocalDateTime.now());
            order.setPayNo((String) result.get("payNo"));
            
            // 7. 处理业务逻辑
            boolean businessResult = processOrderBusiness(order);
            if (!businessResult) {
                log.error("处理订单业务逻辑失败，orderId: {}, orderNo: {}", orderId, order.getOrderNo());
                return false;
            }
            
            // 8. 保存订单
            orderRepository.save(order);
            
            log.info("订单支付成功处理完成，orderId: {}, orderNo: {}, 用户ID: {}", 
                    orderId, order.getOrderNo(), order.getUser().getId());
            
            return true;
            
        } catch (Exception e) {
            log.error("处理订单完成事件失败", e);
            return false;
        }
    }
    
    /**
     * 处理订单退款事件
     */
    private boolean handleOrderRefund(Map<String, Object> callbackData) {
        try {
            // 1. 获取订单信息
            String orderId = (String) callbackData.get("orderId");
            if (orderId == null) {
                log.error("回调数据缺少orderId字段");
                return false;
            }
            
            // 2. 查找本地订单
            Order order = orderRepository.findByOutNo(orderId)
                    .orElse(null);
            
            if (order == null) {
                log.error("未找到对应的本地订单，orderId: {}", orderId);
                return false;
            }
            
            // 3. 检查订单状态
            if (order.getOrderStatus() == Order.OrderStatus.REFUNDED) {
                log.info("订单已经退款，orderId: {}, orderNo: {}", orderId, order.getOrderNo());
                return true;
            }
            
            // 4. 更新订单状态
            order.setOrderStatus(Order.OrderStatus.REFUNDED);
            order.setRemark("订单已退款");
            
            // 5. 处理退款业务逻辑
            boolean businessResult = processRefundBusiness(order);
            if (!businessResult) {
                log.error("处理退款业务逻辑失败，orderId: {}, orderNo: {}", orderId, order.getOrderNo());
                return false;
            }
            
            // 6. 保存订单
            orderRepository.save(order);
            
            log.info("订单退款处理完成，orderId: {}, orderNo: {}, 用户ID: {}", 
                    orderId, order.getOrderNo(), order.getUser().getId());
            
            return true;
            
        } catch (Exception e) {
            log.error("处理订单退款事件失败", e);
            return false;
        }
    }
    
    /**
     * 处理订单业务逻辑
     * 根据订单类型执行相应的业务操作
     */
    private boolean processOrderBusiness(Order order) {
        try {
            User user = order.getUser();
            
            switch (order.getOrderType()) {
                case MOVIE_PURCHASE:
                    return processMoviePurchase(order, user);
                case VIP_PURCHASE:
                    return processVipPurchase(order, user);
                case RECHARGE:
                    return processRecharge(order, user);
                default:
                    log.warn("未处理的订单类型: {}", order.getOrderType());
                    return true;
            }
        } catch (Exception e) {
            log.error("处理订单业务逻辑失败，orderNo: {}", order.getOrderNo(), e);
            return false;
        }
    }
    
    /**
     * 处理电影购买业务
     */
    private boolean processMoviePurchase(Order order, User user) {
        try {
            // 检查是否已购买
            if (userMoviePurchaseRepository.existsByUserIdAndMovieId(user.getId(), order.getProductId())) {
                log.warn("用户已购买过该电影，用户ID: {}, 电影ID: {}", user.getId(), order.getProductId());
                return true; // 已购买也算成功
            }
            
            // 创建购买记录
            UserMoviePurchase purchase = UserMoviePurchase.builder()
                    .user(user)
                    .movie(Movie.builder().id(order.getProductId()).build()) // 使用Movie对象
                    .purchaseTime(LocalDateTime.now())
                    .amount(order.getAmount())
                    .paymentMethod(UserMoviePurchase.PaymentMethod.valueOf(order.getPaymentMethod().name()))
                    .status(UserMoviePurchase.PurchaseStatus.SUCCESS)
                    .build();
            
            userMoviePurchaseRepository.save(purchase);
            
            log.info("电影购买记录创建成功，用户ID: {}, 电影ID: {}, 订单号: {}", 
                    user.getId(), order.getProductId(), order.getOrderNo());
            
            return true;
            
        } catch (Exception e) {
            log.error("处理电影购买业务失败，orderNo: {}", order.getOrderNo(), e);
            return false;
        }
    }
    
    /**
     * 处理VIP购买业务
     */
    private boolean processVipPurchase(Order order, User user) {
        try {
            // 获取VIP套餐信息
            VipPackage vipPackage = vipPackageRepository.findById(order.getProductId())
                    .orElse(null);
            
            if (vipPackage == null) {
                log.error("VIP套餐不存在，套餐ID: {}", order.getProductId());
                return false;
            }
            
            // 计算VIP到期时间
            LocalDateTime vipExpireTime;
            if (user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
                // 如果用户已经是VIP且未过期，在现有到期时间基础上延长
                vipExpireTime = user.getVipExpireTime().plusDays(vipPackage.getDurationDays());
            } else {
                // 如果用户不是VIP或已过期，从当前时间开始计算
                vipExpireTime = LocalDateTime.now().plusDays(vipPackage.getDurationDays());
            }
            
            // 更新用户VIP状态
            user.setVipExpireTime(vipExpireTime);
            user.setIsVip(true);
            userRepository.save(user);
            
            log.info("VIP购买成功，用户ID: {}, 套餐ID: {}, 到期时间: {}, 订单号: {}", 
                    user.getId(), order.getProductId(), vipExpireTime, order.getOrderNo());
            
            return true;
            
        } catch (Exception e) {
            log.error("处理VIP购买业务失败，orderNo: {}", order.getOrderNo(), e);
            return false;
        }
    }
    
    /**
     * 处理充值业务
     */
    private boolean processRecharge(Order order, User user) {
        try {
            // 更新用户余额
            user.setAccountBalance(user.getAccountBalance().add(order.getAmount()));
            userRepository.save(user);
            
            log.info("充值成功，用户ID: {}, 充值金额: {}, 当前余额: {}, 订单号: {}", 
                    user.getId(), order.getAmount(), user.getAccountBalance(), order.getOrderNo());
            
            return true;
            
        } catch (Exception e) {
            log.error("处理充值业务失败，orderNo: {}", order.getOrderNo(), e);
            return false;
        }
    }
    
    /**
     * 处理退款业务逻辑
     */
    private boolean processRefundBusiness(Order order) {
        try {
            User user = order.getUser();
            
            switch (order.getOrderType()) {
                case MOVIE_PURCHASE:
                    // 电影购买退款：删除购买记录
                    userMoviePurchaseRepository.deleteByUserIdAndMovieId(user.getId(), order.getProductId());
                    log.info("电影购买退款处理完成，用户ID: {}, 电影ID: {}", user.getId(), order.getProductId());
                    break;
                    
                case VIP_PURCHASE:
                    // VIP购买退款：减少VIP时长（这里简化处理，实际可能需要更复杂的逻辑）
                    log.info("VIP购买退款处理完成，用户ID: {}, 套餐ID: {}", user.getId(), order.getProductId());
                    break;
                    
                case RECHARGE:
                    // 充值退款：减少余额
                    user.setAccountBalance(user.getAccountBalance().subtract(order.getAmount()));
                    userRepository.save(user);
                    log.info("充值退款处理完成，用户ID: {}, 退款金额: {}, 当前余额: {}", 
                            user.getId(), order.getAmount(), user.getAccountBalance());
                    break;
                    
                default:
                    log.warn("未处理的退款订单类型: {}", order.getOrderType());
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("处理退款业务逻辑失败，orderNo: {}", order.getOrderNo(), e);
            return false;
        }
    }

    /**
     * 解析API响应，返回Map结构
     */
    private Map<String, Object> parseResponse(ResponseEntity<String> response) {
        try {
            if (response != null && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), Map.class);
            }
        } catch (Exception e) {
            log.error("解析API响应失败", e);
        }
        return new HashMap<>();
    }
} 