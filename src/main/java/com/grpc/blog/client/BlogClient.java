package com.grpc.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
  public static void main(String[] args) {
    System.out.println("Hello I'm gRPC client for Blog");

    BlogClient main = new BlogClient();
    main.run();
  }

  public void run() {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();

    System.out.println("Creating a stub");
    //    createBlog(channel);
    //    readBlog(channel);
    //    updateBlog(channel);
    //    deleteBlog(channel);
    listBlog(channel);

    System.out.println("Shutting down channel");
    channel.shutdown();
  }

  private void createBlog(ManagedChannel channel) {
    Blog blog =
        Blog.newBuilder()
            .setAuthorId("Noradomi")
            .setTitle("Title 1")
            .setContent("What are you fighting for ?")
            .build();

    CreateBlogRequest createBlogRequest = CreateBlogRequest.newBuilder().setBlog(blog).build();

    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

    CreateBlogResponse createBlogResponse = blogClient.createBlog(createBlogRequest);

    System.out.println("Received create blog response");
    System.out.println(createBlogResponse.toString());
  }

  private void readBlog(ManagedChannel channel) {
    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
    String blogId = "5f5cdcd3eb199a7b191ac082";
    ReadBlogRequest readBlogRequest = ReadBlogRequest.newBuilder().setBlogId(blogId).build();

    ReadBlogResponse readBlogResponse = blogClient.readBlog(readBlogRequest);

    System.out.println("Response from server: ");
    System.out.println(readBlogResponse.toString());

    String fakeBlogId = "fake_id";
    ReadBlogRequest readBlogRequestNotFound =
        ReadBlogRequest.newBuilder().setBlogId(fakeBlogId).build();

    ReadBlogResponse readBlogResponseNotFound = blogClient.readBlog(readBlogRequestNotFound);
  }

  private void updateBlog(ManagedChannel channel) {
    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

    UpdateBlogRequest updateBlogRequest =
        UpdateBlogRequest.newBuilder()
            .setBlog(
                Blog.newBuilder()
                    .setId("5f5cdcd3eb199a7b191ac082")
                    .setTitle("Title update")
                    .setContent("No content")
                    .setAuthorId("Oda"))
            .build();

    UpdateBlogResponse updateBlogResponse = blogClient.updateBlog(updateBlogRequest);
    System.out.println(updateBlogRequest.toString());
  }

  private void deleteBlog(ManagedChannel channel) {
    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

    DeleteBlogResponse deleteBlogResponse =
        blogClient.deleteBlog(
            DeleteBlogRequest.newBuilder().setBlogId("5f5cdcd3eb199a7b191ac082").build());

    System.out.println(deleteBlogResponse.getBlogId());
  }

  private void listBlog(ManagedChannel channel) {
    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
    blogClient
        .listBlog(ListBlogRequest.newBuilder().build())
        .forEachRemaining(
            listBlogResponse -> {
              System.out.println(listBlogResponse.getBlog());
            });
  }
}
