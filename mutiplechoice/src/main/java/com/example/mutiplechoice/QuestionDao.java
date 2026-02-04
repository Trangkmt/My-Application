package com.example.mutiplechoice;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuestionDao {

    // Thêm 1 câu hỏi
    @Insert
    void insert(Question question);

    // Thêm nhiều câu hỏi
    @Insert
    void insertAll(List<Question> questions);

    // Lấy 1 câu hỏi theo id
    @Query("SELECT * FROM question WHERE id = :questionId")
    Question getById(int questionId);

    // Cập nhật câu hỏi
    @Update
    void update(Question question);

    // Xóa câu hỏi
    @Delete
    void delete(Question question);
}

