package appaanjanda.snooping.domain.product.service;

import appaanjanda.snooping.domain.member.entity.Member;
import appaanjanda.snooping.domain.member.service.MemberService;
import appaanjanda.snooping.domain.product.entity.RecentProduct;
import appaanjanda.snooping.domain.product.repository.*;
import appaanjanda.snooping.domain.search.dto.SearchContentDto;
import appaanjanda.snooping.domain.wishbox.repository.WishboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final ProductSearchService productSearchService;
    private final MemberService memberService;
    private final RecentProductRepository recentProductRepository;
    private final WishboxRepository wishboxRepository;


    // 최근 본 상품 추가
    public void updateRecentProduct(Long memberId, String productCode) {

        Member member = memberService.findMemberById(memberId);
        // 최근 본 상품
        List<RecentProduct> recentProducts = recentProductRepository.findRecentProductsOrderByCreateTime(memberId);

        // 최근 본 상품에 현재 상품이 포함되어 있는지 확인, 없으면 null
        RecentProduct existProduct = recentProducts.stream()
                .filter(search -> search.getProductCode().equals(productCode))
                .findFirst()
                .orElse(null);

        // 있으면 제거
        if(existProduct != null) {
            recentProductRepository.delete(existProduct);
            recentProducts.remove(existProduct);
        }

        // 새로 본 상품 추가
        RecentProduct newWatchProduct = new RecentProduct();
        newWatchProduct.setMember(member);
        newWatchProduct.setProductCode(productCode);
        recentProductRepository.save(newWatchProduct);

        // 최근 본 상품 10개 이상이면 마지막꺼 지우고 추가
        if (recentProducts.size() >= 10) {
            RecentProduct oldestProduct = recentProducts.get(recentProducts.size()-1);
            recentProductRepository.delete(oldestProduct);
        }

    }

    // 최근 본 상품 리스트
    public List<Object> getRecentProduct(Long memberId) {
        // 현재 사용자의 최근 본 상품들
        List<RecentProduct> recentProducts = recentProductRepository.findRecentProductsOrderByCreateTime(memberId);

        // 최근 본 상품의 상세 정보 리스트
        List<Object> products = new ArrayList<>();

        for (RecentProduct recentProduct : recentProducts) {
            // 상품 코드로 상세정보 가져와서 추가
            String productCode = recentProduct.getProductCode();
            SearchContentDto product = productSearchService.searchProductById(productCode, memberId);

            products.add(product);
        }
        return products;
    }

    // 시간 상품 가격
    public List<?> getPriceHistoryByHour(String productId) {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstTime = now.minusHours(48);

        // 반환타입
        Class<?> productType = productSearchService.searchEntityById(productId);

        log.info(String.valueOf(now));
        
        return null;


    }

    // 일간 상품 가격
    public List<?> getPriceHistoryByDay(String productId) {
        return null;

    }

    // 주간 상품가격
    public List<?> getPriceHistoryByWeek(String productId) {
        return null;

    }


}
