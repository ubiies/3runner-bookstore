package com.nhnacademy.bookstore.book.bookCartegory.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.bookCartegory.repository.BookCategoryCustomRepository;
import com.nhnacademy.bookstore.book.category.dto.response.BookDetailCategoryResponse;
import com.nhnacademy.bookstore.entity.book.QBook;
import com.nhnacademy.bookstore.entity.bookCategory.QBookCategory;
import com.nhnacademy.bookstore.entity.bookImage.QBookImage;
import com.nhnacademy.bookstore.entity.bookImage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.category.QCategory;
import com.nhnacademy.bookstore.entity.totalImage.QTotalImage;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

/**
 * book-category query dsl 인터페이스 구현체
 *
 * @author 김은비
 */
@Slf4j
@Repository
public class BookCategoryCustomRepositoryImpl implements BookCategoryCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;
	private final QCategory qCategory = QCategory.category;
	private final QBookCategory qBookCategory = QBookCategory.bookCategory;
	private final QBookImage qBookImage = QBookImage.bookImage;
	private final QBook qBook = QBook.book;
	private final QTotalImage qTotalImage = QTotalImage.totalImage;

	public BookCategoryCustomRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	/**
	 * 카테고리로 도서 조회 메서드
	 *
	 * @param categoryId 조회할 카테고리 아이디
	 * @param pageable   페이지
	 * @return 조회된 도서 list
	 */
	@Override
	public Page<BookListResponse> categoryWithBookList(Long categoryId, Pageable pageable) {
		List<BookListResponse> content = jpaQueryFactory.select(
				Projections.constructor(BookListResponse.class, qBookCategory.book.id, qBookCategory.book.title,
					qBookCategory.book.price,
					qBookCategory.book.sellingPrice, qBookCategory.book.author, qTotalImage.url))
			.from(qBookCategory)
			.join(qBookCategory.book, qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qBook.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qBookImage.id.eq(qTotalImage.bookImage.id))
			.where(qBookCategory.category.id.eq(categoryId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = jpaQueryFactory.select(qBookCategory.count())
			.from(qBookCategory)
			.where(qBookCategory.category.id.eq(categoryId))
			.fetchOne();
		totalCount = totalCount != null ? totalCount : 0L;

		return new PageImpl<>(content, pageable, totalCount);
	}

	/**
	 * 도서 아이디로 카테고리 list 조회 리스트
	 *
	 * @param bookId 도서 아이디
	 * @return 카테고리 list
	 */
	@Override
	public List<BookDetailCategoryResponse> bookWithCategoryList(Long bookId) {
		QCategory qCategoryParent = new QCategory("qCategoryParent");

		return jpaQueryFactory.select(
				Projections.constructor(BookDetailCategoryResponse.class,
					qBookCategory.category.id,
					qBookCategory.category.name,
					new CaseBuilder()
						.when(qBookCategory.category.parent.isNotNull())
						.then(qBookCategory.category.parent.id)
						.otherwise((Long)null)))
			.from(qBookCategory)
			.leftJoin(qBookCategory.category.parent, qCategoryParent)
			.where(qBookCategory.book.id.eq(bookId))
			.fetch();
	}

	/**
	 * 카테고리 리스트로 도서 조회 메서드
	 *
	 * @param categoryList 조회할 카테고리 아이디 리스트
	 * @param pageable     페이지
	 * @return 조회된 도서 list
	 */
	@Override
	public Page<BookListResponse> categoriesWithBookList(List<Long> categoryList, Pageable pageable) {
		List<BookListResponse> content = jpaQueryFactory.select(
				Projections.constructor(BookListResponse.class, qBookCategory.book.id, qBookCategory.book.title,
					qBookCategory.book.price,
					qBookCategory.book.sellingPrice, qBookCategory.book.author, qTotalImage.url))
			.from(qBookCategory)
			.join(qBookCategory.book, qBook)
			.leftJoin(qBookImage)
			.on(qBookImage.book.id.eq(qBook.id).and(qBookImage.type.eq(BookImageType.MAIN)))
			.leftJoin(qTotalImage)
			.on(qBookImage.id.eq(qTotalImage.bookImage.id))
			.where(qBookCategory.category.id.in(categoryList))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		Long totalCount = jpaQueryFactory.select(qBookCategory.count())
			.from(qBookCategory)
			.where(qBookCategory.category.id.in(categoryList))
			.fetchOne();
		totalCount = totalCount != null ? totalCount : 0L;
		return new PageImpl<>(content, pageable, totalCount);
	}
}
