package com.elastic.cspm.data.repository.impl;

import com.elastic.cspm.data.dto.QResourceDto;
import com.elastic.cspm.data.dto.ResourceFilterDto;
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
    public Page<QResourceDto> findResourceList(Pageable pageable, ResourceFilterDto resourceFilterDto) {
        log.info("Searching resources - IAM : {}, ScanGroup : {}, Resource : {}, Service : {}",
                resourceFilterDto.getIAM(), resourceFilterDto.getScanGroup(),
                resourceFilterDto.getResource(), resourceFilterDto.getService());

        List<QResourceDto> content = createResourceDtoQuery()
                .where(
                        iAMEq(resourceFilterDto.getIAM()),
                        scanGroupEq(resourceFilterDto.getScanGroup()),
                        resourceEq(resourceFilterDto.getResource())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // stream().toList();

        log.info("Found {} tickets matching the search criteria.", content.size() );
        log.info("Page Size : {}  and PageOffset : {}.", pageable.getPageSize(), pageable.getPageNumber());

//        return new PageImpl<>(content, pageable, content.size());
        return new PageImpl<>(content);
    }



    private BooleanExpression iAMEq(String iam) {
        if(hasText(iam)){
            return member.iamName.eq(iam);
        } else return null;
    }
    private BooleanExpression scanGroupEq(String group) { // 확실치 않음 수정
        if(hasText(group)){
            return scanGroup.resourceGroupName.eq(group);
        } else return null;
    }
    private BooleanExpression resourceEq(String resource) {
        if(hasText(resource)){
            return describeResult.scanTarget.eq(resource);
        } else return null;
    }

    // 서비스는 데이터 전달을 위한 것이 아닌 프론트에서 편히 보기 위한 것으로 판단해서 일단 제외.
//    private BooleanExpression serviceEq(String service) {
//        if(hasText(service)){
//            return describeResult.
//        } else return null;
//    }

    /**
     * projection qResourceDto 조회
     * service를 알면 수정 -> 프론트에서 선택한 것을 그대로 반환해서 리스트에 적어버리면 되지 않을까 생각.
     * 그러면 이 메서드에서 프로젝션으로 생성하지 않아도 되지 않을까?
     */
    @Transactional
    public JPAQuery<QResourceDto> createResourceDtoQuery() {
        return queryFactory.select(
                        Projections.constructor(QResourceDto.class,
                                describeResult.iam.member.iamName,
                                describeResult.iam.member.groups,
                                describeResult.resourceId,
                                describeResult.scanTarget))
                .from(describeResult)
                .join(describeResult.iam.member, member)
                .join(describeResult.iam.member.groups, scanGroup);

    }
}
