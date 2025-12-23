package com.demo.rest;

import com.demo.model.Todo;
import com.demo.service.TodoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * TODO管理のREST APIエンドポイント
 */
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @Inject
    private TodoService todoService;

    /**
     * 全てのTODOを取得
     * GET /api/todos
     */
    @GET
    public Response getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return Response.ok(todos).build();
    }

    /**
     * IDでTODOを取得
     * GET /api/todos/{id}
     */
    @GET
    @Path("/{id}")
    public Response getTodoById(@PathParam("id") String id) {
        return todoService.getTodoById(id)
                .map(todo -> Response.ok(todo).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Todo not found"))
                        .build());
    }

    /**
     * 象限別にTODOを取得
     * GET /api/todos/quadrant/{quadrant}
     */
    @GET
    @Path("/quadrant/{quadrant}")
    public Response getTodosByQuadrant(@PathParam("quadrant") int quadrant) {
        if (quadrant < 1 || quadrant > 4) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Quadrant must be between 1 and 4"))
                    .build();
        }
        List<Todo> todos = todoService.getTodosByQuadrant(quadrant);
        return Response.ok(todos).build();
    }

    /**
     * 新しいTODOを作成
     * POST /api/todos
     */
    @POST
    public Response createTodo(Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Title is required"))
                    .build();
        }
        Todo createdTodo = todoService.createTodo(todo);
        return Response.status(Response.Status.CREATED)
                .entity(createdTodo)
                .build();
    }

    /**
     * TODOを更新
     * PUT /api/todos/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateTodo(@PathParam("id") String id, Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Title is required"))
                    .build();
        }
        return todoService.updateTodo(id, todo)
                .map(updatedTodo -> Response.ok(updatedTodo).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Todo not found"))
                        .build());
    }

    /**
     * TODOを削除
     * DELETE /api/todos/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteTodo(@PathParam("id") String id) {
        boolean deleted = todoService.deleteTodo(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", "Todo not found"))
                    .build();
        }
    }

    /**
     * 完了状態を切り替え
     * PATCH /api/todos/{id}/toggle
     */
    @PATCH
    @Path("/{id}/toggle")
    public Response toggleCompleted(@PathParam("id") String id) {
        return todoService.toggleCompleted(id)
                .map(todo -> Response.ok(todo).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Todo not found"))
                        .build());
    }

    /**
     * 統計情報を取得
     * GET /api/todos/stats
     */
    @GET
    @Path("/stats")
    public Response getStatistics() {
        Map<String, Object> stats = Map.of(
                "total", todoService.getTodoCount(),
                "completed", todoService.getCompletedCount(),
                "byQuadrant", todoService.getQuadrantStatistics()
        );
        return Response.ok(stats).build();
    }
}

// Made with Bob
