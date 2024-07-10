package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll()
                .stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO getLabelById(Long labelId)
            throws ResourceNotFoundException {
        var label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + labelId + " not found"));
        return labelMapper.map(label);
    }

    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO)
            throws ResourceAlreadyExistsException {
        var label = labelMapper.map(labelCreateDTO);
        try {
            labelRepository.save(label);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException("Label with name '" + label.getName() + "' already exists");
        }
        return labelMapper.map(label);
    }

    public LabelDTO updateLabelById(Long labelId, LabelUpdateDTO labelUpdateDTO)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + labelId + " not found"));
        try {
            labelMapper.update(labelUpdateDTO, label);
            labelRepository.save(label);
        } catch (Exception e) {
            throw new ResourceAlreadyExistsException(e.getMessage());
        }
        return labelMapper.map(label);
    }

    public void deleteLabelById(Long labelId)
            throws ResourceNotFoundException, ResourceAlreadyExistsException {
        var label = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + labelId + " not found"));
        if (label.getTasks().isEmpty()) {
            labelRepository.delete(label);
        } else {
            throw new ResourceAlreadyExistsException("Cannot delete a label because it is bind with task");
        }
    }
}
