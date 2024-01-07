provider "aws" {
  region = "us-east-1"
  access_key = "test"
  secret_key = "test"
  skip_credentials_validation = true
  skip_metadata_api_check = true
  skip_requesting_account_id = true
  s3_use_path_style = true
  endpoints{
    s3 ="http://localhost:4566"
    sqs="http://localhost:4566"
    dynamodb = "http://localhost:4566"
  }
}

resource "aws_s3_bucket" "book_bucket" {
  bucket = "book-bucket"
}
resource "aws_s3_bucket_acl" "s3_bucket" {
  bucket = aws_s3_bucket.book_bucket.id
  acl ="public-read-write"
}
resource "aws_s3_bucket_policy" "s3_bucket_policy" {
  bucket = aws_s3_bucket.book_bucket.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "PublicReadGetObject",
        Effect    = "Allow",
        Principal = "*",
        Action    = "s3:GetObject",
        Resource  = [
          "${aws_s3_bucket.book_bucket.arn}/*",
        ],
      },
    ],
  })
}

resource "aws_dynamodb_table" "book_table"{
  name="book"
  read_capacity = "2"
  write_capacity = "5"
  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }
}

resource "aws_sqs_queue" "queue" {
  name="book-queue"
}