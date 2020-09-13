package com.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {
  private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
  private MongoDatabase database = mongoClient.getDatabase("mydb");
  private MongoCollection<Document> collection = database.getCollection("blog");

  @Override
  public void createBlog(
      CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
    System.out.println("Receive request create a blog");

    Blog blog = request.getBlog();
    Document doc =
        new Document("author_id", blog.getAuthorId())
            .append("title", blog.getTitle())
            .append("content", blog.getContent());

    System.out.println("Inserting blog ...");
    //    insert (create) the document in mongoDB
    collection.insertOne(doc);

    //    We retrieve the document generated ID
    String id = doc.getObjectId("_id").toString();
    System.out.println("Inserted blog with _id = " + id);

    CreateBlogResponse response =
        CreateBlogResponse.newBuilder()
            //            .setBlog(
            //                Blog.newBuilder()
            //                    .setId(id)
            //                    .setAuthorId(blog.getAuthorId())
            //                    .setTitle(blog.getTitle())
            //                    .setContent(blog.getContent())
            //                    .build()) or
            .setBlog(blog.toBuilder().setId(id))
            .build();

    responseObserver.onNext(response);

    responseObserver.onCompleted();
  }

  @Override
  public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
    System.out.println("Received Read Blog request");
    String blogId = request.getBlogId();

    System.out.println("Searching for a blog with blogId = " + blogId);
    Document result = null;
    try {
      result =
          collection
              .find(eq("_id", new ObjectId(blogId))) // find blog by blog id
              .first();
    } catch (Exception e) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding id was not found")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException());
    }

    if (result == null) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding id was not found")
              .asRuntimeException());
    } else {
      System.out.println("Blog found, sending blog");
      Blog blog = documentToBlog(result);

      responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());

      responseObserver.onCompleted();
    }
  }

  @Override
  public void updateBlog(
      UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
    System.out.println("Received Update Blog request");
    Blog updateBlog = request.getBlog();
    System.out.println("Searching for a blog to update");
    Document result = collection.find(eq("_id", new ObjectId(updateBlog.getId()))).first();
    if (result == null) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with corresponding id was not found")
              .asRuntimeException());
    } else {
      Document replacement =
          new Document("author_id", updateBlog.getAuthorId())
              .append("title", updateBlog.getTitle())
              .append("content", updateBlog.getContent())
              .append("_id", new ObjectId(updateBlog.getId()));

      //      Update blog
      System.out.println("Replacing blog in database ...");
      collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

      System.out.println("Replaced! Sending as a response");
      responseObserver.onNext(
          UpdateBlogResponse.newBuilder().setBlog(documentToBlog(replacement)).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void deleteBlog(
      DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
    System.out.println("Received Delete Blog request");

    String blogId = request.getBlogId();
    System.out.println("Searching deleted blog with blogId = " + blogId);

    DeleteResult result = null;
    try {
      result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
    } catch (Exception e) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with corresponding id was not found")
              .asRuntimeException());
    }
    if (result.getDeletedCount() == 0) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with corresponding id was not found")
              .asRuntimeException());
    } else {
      responseObserver.onNext(DeleteBlogResponse.newBuilder().setBlogId(blogId).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
    System.out.println("Received List Blog Request");
    collection.find().iterator().forEachRemaining(document -> {
      responseObserver.onNext(ListBlogResponse.newBuilder().setBlog(documentToBlog(document)).build());
    });
    responseObserver.onCompleted();
  }

  private Blog documentToBlog(Document document) {
    return Blog.newBuilder()
        .setAuthorId(document.getString("author_id"))
        .setTitle(document.getString("title"))
        .setContent(document.getString("content"))
        .setId(document.getObjectId("_id").toString())
        .build();
  }
}
