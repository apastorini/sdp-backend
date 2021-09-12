package com.ude.sdp.service;



import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.model.File;


public interface DocumentService {
	
	public ResultDTO listUserFiles(UserAuthDTO userAuthDTO);
	public ResultDTO existUserFile(UserAuthDTO userAuthDTO,String fileName);
	
	public ResultDTO listSharedFiles(UserAuthDTO userAuthDTO);

	public Resource downloadSharedFiles(UserAuthDTO userAuthDTO, String nameOfFile);

	public File downloadUserFiles(UserAuthDTO userAuthDTO, long fileID);

	public ResultDTO removeFileFromUser(UserAuthDTO userAuthDTO, long fileID);


	public ResultDTO addFileToUser(UserAuthDTO userAuthDTO, MultipartFile file);

	public ResultDTO addFileToSharedFolder(UserAuthDTO userAuthDTO, MultipartFile file);


	


}
