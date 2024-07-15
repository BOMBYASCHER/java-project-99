package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO get(Long userId) throws ResourceNotFoundException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + "not found."));
        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO userCreateDTO) throws ResourceAlreadyExistsException {
        var user = userMapper.map(userCreateDTO);
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        return userMapper.map(user);
    }

    public UserDTO update(Long userId, UserUpdateDTO userUpdateDTO)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + "not found."));
//        try {
            userMapper.update(userUpdateDTO, user);
            userRepository.save(user);
//        } catch (Exception e) {
//            throw new ResourceAlreadyExistsException("User with email '" + user.getEmail() + "' already exists");
//        }
        return userMapper.map(user);
    }

    public void delete(Long userId) throws ResourceAlreadyExistsException {
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(
                    "Cannot delete a user with id " + userId + " because it is bind with another resource"
            );
        }
    }
}
