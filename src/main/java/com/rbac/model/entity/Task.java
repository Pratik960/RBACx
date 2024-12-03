package com.rbac.model.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "tasks"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "int(11) COMMENT 'Unique identifier for the task within RBACx system'")
    private Integer id;

    @Column(name = "title", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT 'Title of the task'")
    private String title;

    @Column(name = "description", nullable = false, length = 255, columnDefinition = "varchar(255) COMMENT 'Description of the task'")
    private String description;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = true)
    private Users assignedUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('CREATED','ASSIGNED','DELETED', 'COMPLETED') DEFAULT 'CREATED' COMMENT 'Current status of the task (e.g., Created, Assigned, Deleted, Completed)'")
    private TaskStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Date and time the user was created'")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Date and time the user was last updated'")
    private Timestamp updatedAt;

    public enum TaskStatus{
        CREATED,
        ASSIGNED,
        DELETED,
        COMPLETED
    } 
}
