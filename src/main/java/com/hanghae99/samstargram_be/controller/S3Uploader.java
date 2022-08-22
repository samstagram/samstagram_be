package com.hanghae99.samstargram_be.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import com.hanghae99.samstargram_be.model.Article;
import com.hanghae99.samstargram_be.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

	private final AmazonS3Client amazonS3Client;
	private final ArticleRepository articleRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String upload(Long articleId, MultipartFile multipartFile, String dirName) throws IOException {
		if(multipartFile != null){
			File uploadFile = convert(multipartFile)
					.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
			return upload(articleId, uploadFile, dirName);
		}else return "파일 업로드를 실패했습니다. 파일을 확인해주세요";
	}

	private String upload(Long articleId, File uploadFile, String dirName) {
		String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();// dirName + "/" +
		String uploadImageUrl = putS3(articleId, uploadFile, fileName);
		removeNewFile(uploadFile);
		return uploadImageUrl;
	}

	private String putS3(Long articleId, File uploadFile, String fileName) {
		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
		String url = amazonS3Client.getUrl(bucket, fileName).toString();

		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다."));
		article.addImage(url);
		articleRepository.save(article);

		return url;
	}

	private void removeNewFile(File targetFile) {
		if (targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		} else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(file.getOriginalFilename());
		if(convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}
