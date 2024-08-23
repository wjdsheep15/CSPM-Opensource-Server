package com.elastic.cspm.data.repository.impl;

import com.elastic.cspm.data.dto.QResourceDto;
import com.elastic.cspm.data.dto.ResourceFilterRequestDto;
import com.elastic.cspm.data.repository.ResourceRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.elastic.cspm.data.entity.QDescribeResult.describeResult;
import static com.elastic.cspm.data.entity.QIAM.iAM;
import static com.elastic.cspm.data.entity.QMember.member;
import static com.elastic.cspm.data.entity.QScanGroup.scanGroup;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Repository
public class ResourceRepositoryCustomImpl implements ResourceRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ResourceRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
//    public ResourceRepositoryCustomImpl(EntityManager em) {
//        this.queryFactory = new JPAQueryFactory(em);
//    }

    /**
     * 모든 검색 필터를 적용해서 리스트 반환하는 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QResourceDto> findResourceList(Pageable pageable, ResourceFilterRequestDto resourceFilterDto) {
        // 필터링 요청 보낸 로그
        log.info("Searching resources - IAM : {}, ScanGroup : {}, Page : {}",
                resourceFilterDto.getIam(), resourceFilterDto.getScanGroup(), pageable);

        // 필터링 쿼리 + 페이징
        List<QResourceDto> content = createResourceDtoQuery()
                .where(
                        iAMEq(resourceFilterDto.getIam()),
                        scanGroupEq(resourceFilterDto.getScanGroup())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // stream().toList();

        log.info("Found {}-ResourceResult", content.size());
        log.info("Page Size : {}  and PageOffset : {}.", pageable.getPageSize(), pageable.getPageNumber());

        return new PageImpl<>(content, pageable, content.size());
//        return new PageImpl<>(content);
    }


    private BooleanExpression iAMEq(String iam) {
        return hasText(iam) ? describeResult.iam.nickName.eq(iam) : null;
    }

    // ScanGroup 테이블에 데이터가 있어야 조회가 됨. (테이블 하나 더 생기므로 수정 필요.)
    private BooleanExpression scanGroupEq(String group) {
        log.info("resourcegroupname : {}", describeResult.iam.member.groups.any().resourceGroupName.eq(group));
        return hasText(group) ? describeResult.iam.member.groups.any().resourceGroupName.eq(group) : null;
//        return hasText(group) ? describeResult.scanGroup.eq(group) : null;
    }


    /**
     * projection qResourceDto 조회
     * service를 알면 수정 -> 프론트에서 선택한 것을 그대로 반환해서 리스트에 적어버리면 되지 않을까 생각.
     * 그러면 이 메서드에서 프로젝션으로 생성하지 않아도 되지 않을까?
     */
    @Transactional
    public JPAQuery<QResourceDto> createResourceDtoQuery() {
        return queryFactory.select(
                        Projections.constructor(QResourceDto.class,
                                describeResult.scanTime,
                                describeResult.iam.member.accountId,
                                describeResult.scanTarget,
                                describeResult.resourceId))
                .from(describeResult)
                .leftJoin(describeResult.iam.member, member);
    }
}
