package appaanjanda.snooping.domain.wishbox.service;

import appaanjanda.snooping.domain.member.entity.Member;
import appaanjanda.snooping.domain.member.repository.MemberRepository;
import appaanjanda.snooping.domain.product.repository.product.DigitalProductRepository;
import appaanjanda.snooping.domain.product.repository.product.FoodProductRepository;
import appaanjanda.snooping.domain.product.repository.product.FurnitureProductRepository;
import appaanjanda.snooping.domain.product.repository.product.NecessariesProductRepository;
import appaanjanda.snooping.domain.wishbox.entity.Wishbox;
import appaanjanda.snooping.domain.wishbox.service.dto.AddWishboxResponseDto;
import appaanjanda.snooping.domain.wishbox.service.dto.RemoveWishboxResponseDto;
import appaanjanda.snooping.external.fastApi.CoupangCrawlingCaller;
import appaanjanda.snooping.external.fastApi.NaverApiCaller;
import appaanjanda.snooping.global.error.code.ErrorCode;
import appaanjanda.snooping.global.error.exception.BusinessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import appaanjanda.snooping.domain.wishbox.repository.WishboxRepository;
import appaanjanda.snooping.domain.wishbox.service.dto.SaveItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WishboxService {

	private final WishboxRepository wishboxRepository;
	private final MemberRepository memberRepository;
	private final CoupangCrawlingCaller coupangCrawlingCaller;
	private final NaverApiCaller naverApiCaller;


	//찜 상품 등록
	public AddWishboxResponseDto addWishbox(Long memberId, String productCode) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_USER_ID));

		Wishbox wishbox = Wishbox.builder()
				.alertPrice(0)
				.alertYn(false)
				.productCode(productCode)
				.member(member)
				.build();
		wishboxRepository.save(wishbox);

		return AddWishboxResponseDto
				.builder()
				.productCode(productCode)
				.alertYn(false)
				.alertPrice(0)
				.build();
	}

	// 찜 상품 목록 조회
	// TODO : 추가적으로 Response DTO 명확하게 수정
	@Transactional(readOnly = true)
	public List<Wishbox> getWishboxList(Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_USER_ID));
		return member.getWishboxList();
	}

	// 찜 상품 삭제
	public RemoveWishboxResponseDto removeWishbox(Long memberId, Long wishboxId) {
		Wishbox wishbox = wishboxRepository.findById(wishboxId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_WISHBOX_ID));
		wishboxRepository.delete(wishbox);

		return RemoveWishboxResponseDto
				.builder()
				.removeId(wishboxId)
				.build();
	}


//
//	// 찜 상품 기져와서 업데이트
//	@Scheduled(cron = "*/10 * * * *")
//	public void wishboxUpdate() {
//		List<Wishbox> allWishbox = wishboxRepository.findAll();
//
//		for (Wishbox wishbox : allWishbox) {
//			String productCode = wishbox.getProductCode();
//			if (wishbox.getProvider().equals("쿠팡")){
//				coupangCrawlingCaller.oneProductSearch(productCode);
//			} else {
//				naverApiCaller.oneProductSearch(productCode);
//			}
//		}
//	}
}
