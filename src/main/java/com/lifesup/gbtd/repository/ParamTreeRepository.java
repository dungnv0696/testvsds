package com.lifesup.gbtd.repository;

import com.lifesup.gbtd.model.ParamTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParamTreeRepository extends JpaRepository<ParamTreeEntity, String>, ParamTreeRepositoryCustom {

}
