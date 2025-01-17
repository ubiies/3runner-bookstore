package com.nhnacademy.bookstore.book.bookCartegory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookCategory.BookCategory;
import com.nhnacademy.bookstore.entity.category.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long>, BookCategoryCustomRepository {
	void deleteByBook(Book book);

	boolean existsByBookAndCategory(Book book, Category category);

	List<BookCategory> findByBookId(long bookId);
}
