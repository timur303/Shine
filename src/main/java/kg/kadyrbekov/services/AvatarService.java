package kg.kadyrbekov.services;

import kg.kadyrbekov.exception.AvatarException;
import kg.kadyrbekov.exception.NotFoundException;
import kg.kadyrbekov.model.User;
import kg.kadyrbekov.model.entity.Image;
import kg.kadyrbekov.repositories.ImagesRepository;
import kg.kadyrbekov.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@Service
public class AvatarService {
    private final ImagesRepository imageRepository;
    private final UserRepository userRepository;

    public AvatarService(ImagesRepository imageRepository, UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }


    public User getAuthentication() throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User with email not found"));
    }

//    @Transactional
//    public Image uploadAvatar(MultipartFile file) throws IOException {
//        User user = getAuthentication();
//
//        if (user.getAvatar() != null) {
//            throw new AvatarException("User already has an avatar. Cannot set a new one.");
//        }
//        Image image = new Image();
//        image.setName(file.getName());
//        image.setOriginalFileName(file.getOriginalFilename());
//        image.setContentType(file.getContentType());
//        image.setSize(file.getSize());
//        image.setBytes(file.getBytes());
//        image.setUser(user);
//
//        user.setAvatar(image);
//
//
//        return imageRepository.save(image);
//    }
public Image uploadAvatar(Long userId, MultipartFile file) throws IOException {
    User user = userRepository.findById(userId).orElseThrow(
            () -> new NotFoundException("User with ID not found"));

    if (user.getAvatar() != null) {
        throw new AvatarException("User already has an avatar. Cannot set a new one.");
    }

    Image image = new Image();
    image.setName(file.getName());
    image.setOriginalFileName(file.getOriginalFilename());
    image.setContentType(file.getContentType());
    image.setSize(file.getSize());
    image.setBytes(file.getBytes());
    image.setUser(user);

    user.setAvatar(image);

    return imageRepository.save(image);
}


    public void updateAvatar(Long avatarID, MultipartFile file) throws IOException, NotFoundException {
        User user = getAuthentication();
        Image image = imageRepository.findById(avatarID)
                .orElseThrow(() -> new NotFoundException("Avatar with ID " + avatarID + " not found."));

        image.setUser(user);
        if (image != null) {
            image.setName(file.getName());
            image.setOriginalFileName(file.getOriginalFilename());
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setBytes(file.getBytes());

            imageRepository.save(image);
        } else {
            throw new NotFoundException("Avatar with ID " + avatarID + " does not have.");
        }
    }


    public void deleteAvatar(Long avatarID) {
        Optional<Image> imageOptional = imageRepository.findById(avatarID);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            imageRepository.delete(image);
        } else {

            throw new IllegalArgumentException("Image with ID " + avatarID + " does not exist");
        }
    }

}
